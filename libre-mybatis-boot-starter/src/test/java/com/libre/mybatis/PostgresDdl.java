package com.libre.mybatis;


import com.baomidou.mybatisplus.extension.ddl.SimpleDdl;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@Component
public class PostgresDdl extends SimpleDdl {

	private final DataSource dataSource;

	public PostgresDdl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
     * 执行 SQL 脚本方式
     */
    @Override
    public List<String> getSqlFiles() {
        return Arrays.asList(
                // 内置包方式
                "db/tag-schema.sql",
                // 文件绝对路径方式（修改为你电脑的地址）
                "db/tag-data.sql"
        );
    }
}
