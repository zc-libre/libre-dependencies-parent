package com.libre.mybatis.permission;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

/**
 * @author ZC
 * @date 2021/11/5 0:00
 */
public class DefaultSqlSource implements SqlSource {

	private final BoundSql boundSql;

	public DefaultSqlSource(BoundSql boundSql) {
		this.boundSql = boundSql;
	}

	@Override
	public BoundSql getBoundSql(Object parameterObject) {
		return this.boundSql;
	}
}
