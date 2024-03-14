package com.libre.mybatis.autoconfigure;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ZC
 * @date 2022/1/14 22:06
 */
@Data
@ConfigurationProperties(prefix = "libre.mybatis")
public class LibreMyBatisProperties {

	/**
	 * 数据库类型
	 */
	private DbType dbType = DbType.MYSQL;

	private Boolean overflow = Boolean.TRUE;

	private Long maxLimit = 500L;

	private DataPermission dataPermission = new DataPermission();

	@Getter
	@Setter
	public static class DataPermission {

		private Boolean enabled = Boolean.FALSE;

	}

}
