package com.libre.mybatis.permission;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.experimental.UtilityClass;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Invocation;

import java.util.function.Function;

/**
 * @author Libre
 */
public class MybatisToolKit {

	public static boolean isUpdate(SqlCommandType sqlCommandType) {
		return SqlCommandType.UPDATE == sqlCommandType || SqlCommandType.INSERT == sqlCommandType;
	}

	public static void convertSql(Invocation invocation, Function<String, String> function) {
		Object[] args = invocation.getArgs();
		MappedStatement mappedStatement = (MappedStatement) args[0];
		BoundSql boundSql = mappedStatement.getBoundSql(args[1]);
		args[0] = buildMappedStatement(mappedStatement,
				new DefaultSqlSource(new BoundSql(mappedStatement.getConfiguration(), function.apply(boundSql.getSql()),
						boundSql.getParameterMappings(), boundSql.getParameterObject())));
	}

	public static MappedStatement buildMappedStatement(MappedStatement mappedStatement, SqlSource sqlSource) {

		MappedStatement.Builder builder = new MappedStatement.Builder(mappedStatement.getConfiguration(),
				mappedStatement.getId(), sqlSource, mappedStatement.getSqlCommandType());

		builder.resource(mappedStatement.getResource());
		builder.parameterMap(mappedStatement.getParameterMap());
		builder.resultMaps(mappedStatement.getResultMaps());
		builder.fetchSize(mappedStatement.getFetchSize());
		builder.timeout(mappedStatement.getTimeout());
		builder.statementType(mappedStatement.getStatementType());
		builder.resultSetType(mappedStatement.getResultSetType());
		builder.cache(mappedStatement.getCache());
		builder.flushCacheRequired(mappedStatement.isFlushCacheRequired());
		builder.useCache(mappedStatement.isUseCache());
		builder.resultOrdered(mappedStatement.isResultOrdered());
		builder.keyGenerator(mappedStatement.getKeyGenerator());
		if (null != mappedStatement.getKeyProperties() && mappedStatement.getKeyProperties().length > 0) {
			builder.keyProperty(String.join(StringPool.COMMA, mappedStatement.getKeyProperties()));
		}

		if (null != mappedStatement.getKeyColumns() && mappedStatement.getKeyColumns().length > 0) {
			builder.keyColumn(String.join(StringPool.COMMA, mappedStatement.getKeyColumns()));
		}

		builder.databaseId(mappedStatement.getDatabaseId());
		builder.lang(mappedStatement.getLang());
		if (null != mappedStatement.getResultSets() && mappedStatement.getResultSets().length > 0) {
			builder.resultSets(String.join(StringPool.COMMA, mappedStatement.getResultSets()));
		}

		return builder.build();
	}

	public static MappedStatement buildMappedStatement(MappedStatement mappedStatement, BoundSql boundSql) {
		return buildMappedStatement(mappedStatement, new DefaultSqlSource(boundSql));
	}

}
