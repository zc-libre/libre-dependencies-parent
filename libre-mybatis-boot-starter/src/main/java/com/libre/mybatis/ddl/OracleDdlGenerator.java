package com.libre.mybatis.ddl;

import java.util.function.Function;

public class OracleDdlGenerator implements IDdlGenerator {

	public OracleDdlGenerator() {
	}

	public static IDdlGenerator newInstance() {
		return new OracleDdlGenerator();
	}

	public boolean existTable(String tableSchema, Function<String, Boolean> function) {
		return function
				.apply("SELECT COUNT(1) AS NUM FROM user_tables WHERE table_name='" + this.getDdlHistory() + "'");
	}

	public String getDdlHistory() {
		return "DDL_HISTORY";
	}

	public String createDdlHistory() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("CREATE TABLE ").append(this.getDdlHistory()).append("(");
		buffer.append("script NVARCHAR2(500) NOT NULL,");
		buffer.append("type NVARCHAR2(30) NOT NULL,");
		buffer.append("version NVARCHAR2(30) NOT NULL");
		buffer.append(");");
		return buffer.toString();
	}

}
