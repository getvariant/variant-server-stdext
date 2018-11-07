package com.variant.extapi.standard.flush.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

import com.typesafe.config.Config;
import com.variant.server.api.ServerException;
import com.variant.server.api.TraceEventFlusher;
import com.variant.server.jdbc.JdbcService.Vendor;

/**
 * An implementation of {@link TraceEventFlusher}, which writes trace events to an 
 * instance of MySql database. The required database schema can be created by the
 * {@code create-schema.sql} SQL script, included with Variant server. 
 * <p>
 * Configuration.<br/>You may use the <code>variant.event.flusher.class.init</code> configuration property to pass configuration details to this object.
 * 
 * <ul>
 *  <li><code>url</code> - specifies the JDBC URL to the MySql database.
 *  <li><code>user</code> - the Postgres database user.
 *  <li><code>password</code> - the Postgres database user's password.
 * </ul>
 * Example:<br/>
 * <code>variant.event.flusher.class.init = {"url":"jdbc:postgresql://localhost/variant","user":"variant","password":"variant"}</code>
 * @since 0.5
 */
public class TraceEventFlusherMysql extends TraceEventFlusherJdbc {
	
	private Connection conn = null;

	public TraceEventFlusherMysql(Config config) throws Exception {
				
		String url = config.getString("url");
		if (url == null) throw new ServerException("Missing configuration property [url]");

		conn = DriverManager.getConnection(url);		
		
	}

	@Override
	public Connection getJdbcConnection() throws Exception {
		return conn;
	}

	@Override
	protected Vendor getJdbcVendor() {
		return Vendor.POSTGRES;
	}

}
