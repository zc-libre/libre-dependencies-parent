package com.libre.mybatis.ddl;

import java.util.function.Function;

public interface IDdlGenerator {

	boolean existTable(String tableSchema, Function<String, Boolean> function);

	default String getDdlHistory() {
		return "ddl_history";
	}

	String createDdlHistory();

	default String selectDdlHistory(String script, String ddlType) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT version FROM ").append(this.getDdlHistory()).append(" WHERE script='").append(script);
		buffer.append("' AND type='").append(ddlType).append("'");
		return buffer.toString();
	}

	default String insertDdlHistory(String script, String version, String type) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("INSERT INTO ").append(this.getDdlHistory()).append("(script,type,version) VALUES ('");
		buffer.append(script).append("','").append(version).append("','").append(type).append("')");
		return buffer.toString();
	}

}
