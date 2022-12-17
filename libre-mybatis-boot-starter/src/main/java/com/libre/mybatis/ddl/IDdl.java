package com.libre.mybatis.ddl;

import javax.sql.DataSource;
import java.util.List;
import java.util.function.Consumer;

public interface IDdl {

	void runScript(Consumer<DataSource> dataSourceConsumer);

	default IDdlGenerator getDdlGenerator() {
		return null;
	}

	List<String> getSqlFiles();

}
