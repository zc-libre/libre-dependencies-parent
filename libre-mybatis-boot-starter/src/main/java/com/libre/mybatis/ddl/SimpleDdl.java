package com.libre.mybatis.ddl;

import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class SimpleDdl implements IDdl {

	private final DataSource dataSource;


	public void runScript(Consumer<DataSource> dataSourceConsumer) {
		dataSourceConsumer.accept(this.dataSource);
	}

	public List<String> getSqlFiles() {
		return null;
	}

}
