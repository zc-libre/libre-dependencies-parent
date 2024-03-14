package com.libre.mybatis.permission;

import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.libre.mybatis.annotation.DataScope;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Libre
 */
@Slf4j
public class DataScopePropertyHandler {

	private static Map<String, DataScopeProperty> dataScopePropertyMap;

	public static void initDataScopePropertyMap() {
		dataScopePropertyMap = new ConcurrentHashMap<>(16);
	}

	@SuppressWarnings({ "unchecked" })
	public static DataScopeProperty getDataScopeProperty(String statementId) throws Exception {
		DataScopeProperty dataScopeProperty = dataScopePropertyMap.get(statementId);
		if (null == dataScopeProperty) {
			String entityClassName = statementId.substring(0, statementId.lastIndexOf(StringPool.DOT));

			if (null == entityClassName
					|| DataScopeProperty.CHECK_INSTANCE == dataScopePropertyMap.get(entityClassName)) {
				return null;
			}

			Class clazz = Class.forName(entityClassName);
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				DataScope dataScope = method.getAnnotation(DataScope.class);
				if (null != dataScope) {
					dataScopePropertyMap.put(entityClassName + "." + method.getName(),
							new DataScopeProperty(dataScope));
				}
			}

			DataScope dataScope = (DataScope) clazz.getAnnotation(DataScope.class);
			dataScopePropertyMap.put(entityClassName,
					null == dataScope ? DataScopeProperty.CHECK_INSTANCE : new DataScopeProperty(dataScope));
			dataScopeProperty = dataScopePropertyMap.get(statementId);
		}

		return dataScopeProperty;
	}

	public static void processStatements(Object[] parameterObjects, MappedStatement mappedStatement,
			StatementProcessor process) {
		BoundSql boundSql = mappedStatement.getBoundSql(parameterObjects[1]);
		String sql = boundSql.getSql();
		if (log.isDebugEnabled()) {
			log.debug("original SQL: " + sql);
		}

		try {
			StringBuffer sb = new StringBuffer();
			Statements statements = CCJSqlParserUtil.parseStatements(sql);
			int i = 0;
			for (Statement statement : statements.getStatements()) {
				if (i > 0) {
					sb.append(StringPool.SEMICOLON);
				}
				process.process(statement, i);
				sb.append(statement);
				i++;
			}

			parameterObjects[0] = MybatisToolKit.buildMappedStatement(mappedStatement,
					new BoundSql(mappedStatement.getConfiguration(), sb.toString(), boundSql.getParameterMappings(),
							boundSql.getParameterObject()));
			if (log.isDebugEnabled()) {
				log.debug("dataScope execute sql: {}", sb);
			}

		}
		catch (JSQLParserException e) {
			throw ExceptionUtils.mpe("Failed to process, Error SQL: %s", e.getCause(), sql);
		}
	}

}
