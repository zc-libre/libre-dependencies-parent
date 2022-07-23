package com.libre.mybatis.permission;

import net.sf.jsqlparser.statement.Statement;

/**
 * @author ZC
 * @date 2021/11/4 23:53
 */
@FunctionalInterface
public interface StatementProcessor {
	/**
	 * 处理sql语句
	 * @param statement /
	 * @param argSeq /
	 */
	void process(Statement statement, int argSeq);
}
