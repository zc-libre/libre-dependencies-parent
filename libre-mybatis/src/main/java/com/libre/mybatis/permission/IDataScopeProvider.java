package com.libre.mybatis.permission;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * @author Libre
 */
public interface IDataScopeProvider {

	/**
	 * sqlRender
	 * @param obj /
	 * @param mappedStatement /
	 * @param sqlCommandType /
	 * @throws Exception /
	 */
	void sqlRender(Object[] obj, MappedStatement mappedStatement, SqlCommandType sqlCommandType) throws Exception;

}
