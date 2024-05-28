package com.variant.spi.stdlib.flush.jdbc;

import com.variant.share.yaml.YamlNode;

public class TraceEventFlusherMysql extends TraceEventFlusherJdbc {
	public TraceEventFlusherMysql(YamlNode<?> init) throws Exception {
		super(init);
	}
	@Override
	protected JdbcVendor getJdbcVendor() {
		return JdbcVendor.MYSQL;
	}

}
