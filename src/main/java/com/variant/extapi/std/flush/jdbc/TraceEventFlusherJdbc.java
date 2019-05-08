package com.variant.extapi.std.flush.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

import com.variant.core.schema.Variation.Experience;
import com.variant.server.api.FlushableTraceEvent;
import com.variant.server.api.TraceEventFlusher;


/**
 * JDBC event flushers extend this class instead of implementing the EventFlusher interface. 
 * All the JDBC work is done here, leaving the concrete subclasses with just the task of
 * creating a database connection for the particular JDBC implementation.
 * 
 * @author Igor.
 *
 */
abstract public class TraceEventFlusherJdbc implements TraceEventFlusher {
				
	/**
	 * Concrete subclass tells this class how to obtain a connection to its flavor of JDBC.
	 * JUnits will also use this to create the schema.
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract Connection getJdbcConnection() throws Exception;

	/**
	 * Implementations will know the vendor.
	 * @return
	 */
	protected abstract JdbcVendor getJdbcVendor();
	
	/**
	 * Persist a collection of events.
	 */
	@Override
	final public void flush(final Collection<FlushableTraceEvent> events) throws Exception {
				
		final String INSERT_EVENTS_SQL = 
				"INSERT INTO events (id, session_id, created_on, event_name) VALUES (?, ?, ?, ?)";

		final String INSERT_EVENT_EXPERIENCES_SQL = 
				"INSERT INTO event_experiences (event_id, variation_name, experience_name, is_control) VALUES (?, ?, ?, ?)"; 

		final String INSERT_EVENT_PARAMETERS_SQL = 
				"INSERT INTO event_attributes (event_id, name, value) VALUES (?, ?, ?)";

		JdbcAdapter.executeUpdate(
			getJdbcConnection(), 
			new JdbcAdapter.UpdateOperation() {

				@Override
				public void execute(Connection conn) throws SQLException {

					//
					// 1. Insert into EVENTS and get the sequence generated IDs back.
					//
					
					PreparedStatement stmt = conn.prepareStatement(INSERT_EVENTS_SQL, Statement.RETURN_GENERATED_KEYS);

					for (FlushableTraceEvent event: events) {
						stmt.setString(1, event.getId());
						stmt.setString(2, event.getSessionId());
						stmt.setTimestamp(3, Timestamp.from(event.getTimestamp()));
						stmt.setString(4, event.getName());

						stmt.addBatch();
					}
					
					// Send rows to the database.
					stmt.executeBatch();
					stmt.close();

					//
					// 2. Insert into EVENT_PARAMETERS.
					//
					stmt = conn.prepareStatement(INSERT_EVENT_PARAMETERS_SQL);
					for (FlushableTraceEvent event: events) {
						for (Map.Entry<String, String> param: event.getAttributes().entrySet()) {
							stmt.setString(1, event.getId());
							stmt.setString(2, param.getKey());
							stmt.setString(3, param.getValue().toString());

							stmt.addBatch();
						}
					}
					
					stmt.executeBatch();
					stmt.close();
					
					//
					// 3. Insert into EVENT_EXPERIENCES.
					//
					stmt = conn.prepareStatement(INSERT_EVENT_EXPERIENCES_SQL);
					for (FlushableTraceEvent event: events) {
						for (Experience exp: event.getLiveExperiences()) {
							stmt.setString(1, event.getId());
							stmt.setString(2, exp.getVariation().getName());
							stmt.setString(3, exp.getName());
							stmt.setBoolean(4, exp.isControl());						
							stmt.addBatch();
						}
					}
					
					stmt.executeBatch();					
					stmt.close();
				}
			}
		);
		
	}
}
