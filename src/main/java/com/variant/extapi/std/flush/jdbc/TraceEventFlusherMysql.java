package com.variant.extapi.std.flush.jdbc;

public class TraceEventFlusherMysql extends TraceEventFlusherJdbc {
	public TraceEventFlusherMysql(String init) throws Exception {
		super(init);
	}
	@Override
	protected JdbcVendor getJdbcVendor() {
		return JdbcVendor.MYSQL;
	}

}
