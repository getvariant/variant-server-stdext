package com.variant.extapi.standard.flush.jdbc;


import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import com.typesafe.config.Config;
import com.variant.server.api.ServerException;
import com.variant.server.api.TraceEventFlusher;
import com.variant.server.jdbc.JdbcService.Vendor;

/**
 * An implementation of {@link TraceEventFlusher}, which writes trace events to an 
 * instance of H2 database. The required database schema can be created by the
 * {@code create-schema.sql} SQL script, included with Variant server. 
 * <p>
 * Configuration.<br/>You may use the <code>variant.event.flusher.class.init</code> configuration property to pass configuration details to this object.
 * 
 * <ul>
 *  <li><code>url</code> - specifies the URL to the H2 database.
 *  <li><code>user</code> - the H2 database user.
 *  <li><code>password</code> - the H2 database user's password.
 * </ul>
 * Example:<br/>
 * <code>variant.event.flusher.class.init = {"url":"jdbc:h2:mem:variant;MVCC=true;DB_CLOSE_DELAY=-1;","user":"variant","password":"variant"}</code>
 * 
 * @since 0.5
 */
public class TraceEventFlusherH2 extends TraceEventFlusherJdbc {
	
	private Connection conn = null;

	public TraceEventFlusherH2(Config config) throws Exception {
				
		String url = config.getString("url");
		if (url == null) throw new ServerException("Missing configuration property [url]");


		String user = config.getString("user");
		if (user == null) throw new ServerException("Missing configuration property [user]");

		String password = config.getString("password");
		if (password == null) throw new ServerException("Missing configuration property [password]");

		Properties props = new Properties();
		props.setProperty("user", user);
		props.setProperty("password", password);
		conn = DriverManager.getConnection(url, props);		
		
	}

	@Override
	public Connection getJdbcConnection() throws Exception {
		return conn;
	}

	@Override
	protected Vendor getJdbcVendor() {
		return Vendor.H2;
	}
	
}
