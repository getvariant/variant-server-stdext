package com.variant.extapi.std.flush.jdbc;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.variant.server.api.ServerException;
import com.variant.server.api.TraceEventFlusher;

public class TraceEventFlusherH2 extends TraceEventFlusherJdbc {

	public TraceEventFlusherH2(String init) throws Exception {
		super(init);
	}

	@Override
	protected JdbcVendor getJdbcVendor() {
		return JdbcVendor.H2;
	}
	
}
