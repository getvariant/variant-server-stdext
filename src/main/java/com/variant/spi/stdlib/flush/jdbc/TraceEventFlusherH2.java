package com.variant.spi.stdlib.flush.jdbc;


import com.variant.share.yaml.YamlNode;

public class TraceEventFlusherH2 extends TraceEventFlusherJdbc {

	public TraceEventFlusherH2(YamlNode<?> init) throws Exception {
		super(init);
	}

	@Override
	protected JdbcVendor getJdbcVendor() {
		return JdbcVendor.H2;
	}
	
}
