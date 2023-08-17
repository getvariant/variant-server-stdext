package com.variant.spi.stdlib.flush.jdbc;


public class TraceEventFlusherH2 extends TraceEventFlusherJdbc {

	public TraceEventFlusherH2(String init) throws Exception {
		super(init);
	}

	@Override
	protected JdbcVendor getJdbcVendor() {
		return JdbcVendor.H2;
	}
	
}
