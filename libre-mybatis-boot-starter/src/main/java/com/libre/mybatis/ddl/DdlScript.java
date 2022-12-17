package com.libre.mybatis.ddl;

import javax.sql.DataSource;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class DdlScript {

	private final DataSource dataSource;

	private final IDdlGenerator ddlGenerator;

	private final boolean autoCommit;

	public DdlScript(DataSource dataSource) {
		this(dataSource, null);
	}

	public DdlScript(DataSource dataSource, IDdlGenerator ddlGenerator) {
		this(dataSource, ddlGenerator, false);
	}

	public DdlScript(DataSource dataSource, IDdlGenerator ddlGenerator, boolean autoCommit) {
		this.dataSource = dataSource;
		this.ddlGenerator = ddlGenerator;
		this.autoCommit = autoCommit;
	}

	public void run(List<String> sqlFiles) {
		this.run(sqlFiles, this.autoCommit);
	}

	public void run(List<String> sqlFiles, boolean autoCommit) {
		DdlRunner.runScript(this.ddlGenerator, this.dataSource, sqlFiles, autoCommit);
	}

	public void run(String sqlFile) throws Exception {
		this.run(new StringReader(sqlFile));
	}

	public void run(Reader reader) throws Exception {
		this.run(reader, this.autoCommit);
	}

	public void run(Reader reader, boolean autoCommit) throws Exception {
		DdlRunner.buildScriptRunner(this.dataSource.getConnection(), autoCommit).runScript(reader);
	}

}
