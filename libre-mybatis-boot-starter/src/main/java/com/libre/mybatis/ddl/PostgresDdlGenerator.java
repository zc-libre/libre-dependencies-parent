package com.libre.mybatis.ddl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.function.Function;

public class PostgresDdlGenerator implements IDdlGenerator {
    public PostgresDdlGenerator() {
    }

    public static IDdlGenerator newInstance() {
        return new PostgresDdlGenerator();
    }

    public boolean existTable(String tableSchema, Function<String, Boolean> function) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT COUNT(1) AS NUM from INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='");
        buffer.append(this.getDdlHistory()).append("' AND TABLE_TYPE='BASE TABLE'");
        if (StringUtils.isNotBlank(tableSchema)) {
            buffer.append(" AND TABLE_SCHEMA='").append(this.getSchema()).append("'");
        }

        return function.apply(buffer.toString());
    }

    public String createDdlHistory() {
        StringBuilder buffer = new StringBuilder();
        String ddlHistory = this.getDdlHistory();
        buffer.append("CREATE TABLE IF NOT EXISTS ").append(ddlHistory).append(" (");
        buffer.append("\"script\" varchar(500) NOT NULL,");
        buffer.append("\"type\" varchar(30) NOT NULL,");
        buffer.append("\"version\" varchar(30) NOT NULL");
        buffer.append(");");
        buffer.append("COMMENT ON COLUMN ").append(ddlHistory).append(".\"script\" IS '脚本';");
        buffer.append("COMMENT ON COLUMN ").append(ddlHistory).append(".\"type\" IS '类型';");
        buffer.append("COMMENT ON COLUMN ").append(ddlHistory).append(".\"version\" IS '版本';");
        buffer.append("COMMENT ON TABLE ").append(ddlHistory).append(" IS 'DDL 版本';");
        return buffer.toString();
    }

    protected String getSchema() {
        return "public";
    }
}
