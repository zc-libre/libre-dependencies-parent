package com.libre.mybatis.permission;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Objects;
import java.util.Properties;

/**
 * @author ZC
 * @date 2021/11/5 0:59
 */
@Intercepts({@Signature(
	type = Executor.class,
	method = "update",
	args = {MappedStatement.class, Object.class}
), @Signature(
	type = Executor.class,
	method = "query",
	args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
), @Signature(
	type = Executor.class,
	method = "query",
	args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
)})
public class DataScopeInterceptor implements Interceptor {

	private IDataScopeProvider dataScopeProvider;

	public DataScopeInterceptor(IDataScopeProvider dataScopeProvider) {
		if (Objects.nonNull(dataScopeProvider)) {
			this.dataScopeProvider = dataScopeProvider;
		}
		DataScopePropertyHandler.initDataScopePropertyMap();
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		return process(invocation, dataScopeProvider);
	}

	@Override
	public Object plugin(Object target) {
		return target instanceof Executor ? Plugin.wrap(target, this) : target;
	}

	@Override
	public void setProperties(Properties properties) {

	}

	public static Object process(Invocation invocation, IDataScopeProvider dataScopeProvider) throws Throwable {
		Object[] args = invocation.getArgs();
		MappedStatement mappedStatement = (MappedStatement)args[0];
		SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
		if (sqlCommandType != SqlCommandType.UNKNOWN && sqlCommandType != SqlCommandType.FLUSH) {
			if (null != dataScopeProvider) {
				dataScopeProvider.sqlRender(args, mappedStatement, sqlCommandType);
			}
		}
		return invocation.proceed();
	}
}
