package com.libre.mybatis.ddl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.function.Function;

public class MysqlDdlGenerator implements IDdlGenerator {
    public MysqlDdlGenerator() {
    }

    public static IDdlGenerator newInstance() {
        return new MysqlDdlGenerator();
    }

    public boolean existTable(String tableSchema, Function<String, Boolean> function) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SELECT COUNT(1) AS NUM from INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='");
        buffer.append(this.getDdlHistory()).append("' AND TABLE_TYPE='BASE TABLE'");
        if (StringUtils.isNotBlank(tableSchema)) {
            buffer.append(" AND TABLE_SCHEMA='").append(tableSchema).append("'");
        }

        return function.apply(buffer.toString());
    }

    public String createDdlHistory() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CREATE TABLE IF NOT EXISTS `").append(this.getDdlHistory()).append("` (");
        buffer.append("`script` varchar(500) NOT NULL COMMENT '脚本',");
        buffer.append("`type` varchar(30) NOT NULL COMMENT '类型',");
        buffer.append("`version` varchar(30) NOT NULL COMMENT '版本',");
        buffer.append("PRIMARY KEY (`script`)");
        buffer.append(") COMMENT = 'DDL 版本';");
        return buffer.toString();
    }
}
