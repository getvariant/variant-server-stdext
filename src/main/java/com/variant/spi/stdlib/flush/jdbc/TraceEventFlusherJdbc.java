package com.variant.spi.stdlib.flush.jdbc;

import java.sql.*;
import java.util.Map;
import java.util.Properties;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.variant.server.spi.ServerException;
import com.variant.share.schema.Variation.Experience;
import com.variant.server.spi.FlushableTraceEvent;
import com.variant.server.spi.TraceEventFlusher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * JDBC event flushers extend this class instead of implementing the EventFlusher interface. 
 * All the JDBC work is done here, leaving the concrete subclasses with just the task of
 * creating a database connection for the particular JDBC implementation.
 * 
 * @author Igor.
 *
 */
abstract public class TraceEventFlusherJdbc implements TraceEventFlusher {

	public final Connection connection;

	/**
	 * The required database schema can be created by the
	 * {@code create-schema.sql} SQL script, included with Variant server.
	 * <p>
	 * Configuration.<br/>You may use the <code>variant.event.flusher.class.init</code> configuration property to pass configuration details to this object.
	 *
	 * <ul>
	 *  <li><code>url</code> - specifies the JDBC URL to the database.
	 *  <li><code>user</code> - the database user.
	 *  <li><code>password</code> - the database user's password.
	 * </ul>
	 * Example:<br/>
	 * <code>variant.event.flusher.class.init = {url:"jdbc:h2:mem:variant;MVCC=true;DB_CLOSE_DELAY=-1;", user: variant, password: variant}</code>
	 *
	 * @since 0.5
	 */
	protected TraceEventFlusherJdbc(String init) throws SQLException {
		Config config = ConfigFactory.parseString(init);

		String url = config.getString("url");
		if (url == null) throw new ServerException("Missing configuration property [url]");


		String user = config.getString("user");
		if (user == null) throw new ServerException("Missing configuration property [user]");

		String password = config.getString("password");
		if (password == null) throw new ServerException("Missing configuration property [password]");

		Properties props = new Properties();
		props.setProperty("user", user);
		props.setProperty("password", password);
		connection = DriverManager.getConnection(url, props);
	}

	@Override
	public void destroy() throws Exception {
		connection.close();
	}

	/**
	 * Implementations will know the vendor.
	 * @return
	 */
	protected abstract JdbcVendor getJdbcVendor();

	/**
	 * Persist a collection of events.
	 */
	@Override
	final public void flush(FlushableTraceEvent[] events, int size) throws Exception {

		final String INSERT_EVENTS_SQL = 
				"INSERT INTO events (id, session_id, created_on, event_name) VALUES (?, ?, ?, ?)";

		final String INSERT_EVENT_EXPERIENCES_SQL = 
				"INSERT INTO event_experiences (event_id, variation_name, experience_name, is_control) VALUES (?, ?, ?, ?)"; 

		final String INSERT_EVENT_ATTRIBUTES_SQL =
				"INSERT INTO event_attributes (event_id, name, value) VALUES (?, ?, ?)";

		JdbcAdapter.executeUpdate(
			connection,
			new JdbcAdapter.UpdateOperation() {

				@Override
				public void execute(Connection conn) throws SQLException {

					//
					// 1. Insert into EVENTS.
					//
					PreparedStatement stmt = conn.prepareStatement(INSERT_EVENTS_SQL, Statement.RETURN_GENERATED_KEYS);
					for (int i = 0; i < size; i++) {
						FlushableTraceEvent event = events[i];
						stmt.setString(1, event.getId());
						stmt.setString(2, event.getSessionId());
						stmt.setTimestamp(3, Timestamp.from(event.getTimestamp()));
						stmt.setString(4, event.getName());
						stmt.execute();
						stmt.clearParameters();
					}
					stmt.close();
					conn.commit();

					//
					// 2. Insert into EVENT_ATTRIBUTES.
					//
					stmt = conn.prepareStatement(INSERT_EVENT_ATTRIBUTES_SQL);
					for (int i = 0; i < size; i++) {
						FlushableTraceEvent event = events[i];
						for (Map.Entry<String, String> param: event.getAttributes().entrySet()) {
							stmt.setString(1, event.getId());
							stmt.setString(2, param.getKey());
							stmt.setString(3, param.getValue());
							stmt.execute();
							stmt.clearParameters();
						}
					}
					stmt.close();
					
					//
					// 3. Insert into EVENT_EXPERIENCES.
					//
					stmt = conn.prepareStatement(INSERT_EVENT_EXPERIENCES_SQL);
					for (int i = 0; i < size; i++) {
						FlushableTraceEvent event = events[i];
						for (Experience exp: event.getLiveExperiences()) {
							stmt.setString(1, event.getId());
							stmt.setString(2, exp.getVariation().getName());
							stmt.setString(3, exp.getName());
							stmt.setBoolean(4, exp.isControl());						
							stmt.execute();
							stmt.clearParameters();
						}
					}
					stmt.close();

					conn.commit();
				}
			}
		);
	}	
}
