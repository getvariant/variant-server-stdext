package com.variant.spi.stdlib.flush.jdbc;

import com.variant.server.spi.TraceEventFlusher;

/**
 * An implementation of {@link TraceEventFlusher}, which writes trace events to an 
 * instance of PostgreSQL database. The required database schema can be created by the
 * {@code create-schema.sql} SQL script, included with Variant server. 
 * <p>
 * Configuration.<br/>You may use the <code>variant.event.flusher.class.init</code> configuration property to pass configuration details to this object.
 * 
 * <ul>
 *  <li><code>url</code> - specifies the URL to the Postgres database.
 *  <li><code>user</code> - the Postgres database user.
 *  <li><code>password</code> - the Postgres database user's password.
 * </ul>
 * Example:<br/>
 * <code>variant.event.flusher.class.init = {"url":"jdbc:postgresql://localhost/variant","user":"variant","password":"variant"}</code>
 * @since 0.5
 */
public class TraceEventFlusherPostgres extends TraceEventFlusherJdbc {
	public TraceEventFlusherPostgres(String init) throws Exception {
		super(init);
	}
	@Override
	protected JdbcVendor getJdbcVendor() {
		return JdbcVendor.POSTGRES;
	}

}
