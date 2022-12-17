package com.libre.mybatis.ddl;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.jdbc.SqlRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
public class DdlRunner {

	public static void runScript(IDdlGenerator ddlGenerator, DataSource dataSource, List<String> sqlFiles,
			boolean autoCommit) {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			String dataSourceUrl = connection.getMetaData().getURL();
			String tableSchema = getTableSchema(dataSourceUrl);
			SqlRunner sqlRunner = new SqlRunner(connection);
			ScriptRunner scriptRunner = buildScriptRunner(connection, autoCommit);
			if (null == ddlGenerator) {
				ddlGenerator = getDdlGenerator(dataSourceUrl);
			}

			if (!ddlGenerator.existTable(tableSchema, (sql) -> {
				try {
					Map<String, Object> resutlMap = sqlRunner.selectOne(sql);
					if (null != resutlMap && !"0".equals(String.valueOf(resutlMap.get("NUM")))) {
						return true;
					}
				}
				catch (SQLException e) {
					log.error("run script sql:{} , error: {}", sql, e);
				}
				return false;
			})) {
				scriptRunner.runScript(new StringReader(ddlGenerator.createDdlHistory()));
			}

			for (String sqlFilePath : sqlFiles) {
				try {
					List<Map<String, Object>> resultMapList = sqlRunner
							.selectAll(ddlGenerator.selectDdlHistory(sqlFilePath, "sql"));
					if (null == resultMapList || resultMapList.isEmpty()) {
						log.debug("run script file: {}", sqlFilePath);
						File sqlFile = new File(sqlFilePath);
						if (sqlFile.exists()) {
							scriptRunner.runScript(new FileReader(sqlFile));
						}
						else {
							scriptRunner.runScript(new InputStreamReader(readSqlFile(sqlFilePath)));
						}
						sqlRunner.insert(ddlGenerator.insertDdlHistory(sqlFilePath, "sql", formatTime()));
					}
				}
				catch (Exception var13) {
					log.error("run script sql:{} , error: {} , Please check if the table `ddl_history` exists",
							sqlFilePath, var13);
				}
			}
		}
		catch (Exception e) {
			log.error("run script error: ", e);
		}
		finally {
			JdbcUtils.closeConnection(connection);
		}

	}

	public static InputStream readSqlFile(String sqlFile) throws Exception {
		return (new ClassPathResource(sqlFile)).getInputStream();
	}

	protected static String formatTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
	}

	public static ScriptRunner buildScriptRunner(Connection connection, boolean autoCommit) {
		ScriptRunner scriptRunner = new ScriptRunner(connection);
		scriptRunner.setAutoCommit(autoCommit);
		scriptRunner.setStopOnError(true);
		return scriptRunner;
	}

	protected static IDdlGenerator getDdlGenerator(String jdbcUrl) {
		jdbcUrl = jdbcUrl.toLowerCase();
		DbType dbType = com.baomidou.mybatisplus.extension.toolkit.JdbcUtils.getDbType(jdbcUrl);

		if (DbType.MYSQL.equals(dbType)) {
			return MysqlDdlGenerator.newInstance();
		}
		if (DbType.POSTGRE_SQL.equals(dbType)) {
			return PostgresDdlGenerator.newInstance();
		}
		if (DbType.ORACLE.equals(dbType) || DbType.ORACLE_12C.equals(dbType)) {
			return OracleDdlGenerator.newInstance();
		}

		throw new RuntimeException("暂不支持该数据库类型, jdbcUrl: " + jdbcUrl);
	}

	public static String getTableSchema(String var0) {
		String[] var1 = var0.split("://");
		if (var1.length == 2) {
			String[] var2 = var1[1].split("/");
			if (var2.length > 1) {
				return var2[1].split("\\?")[0];
			}
		}

		return null;
	}

}
