package com.libre.ip2region.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * ip2region 配置类
 *
 * @author L.cm
 */
@Getter
@Setter
@RefreshScope
@ConfigurationProperties(Ip2regionProperties.PREFIX)
public class Ip2regionProperties {
	public static final String PREFIX = "libre.ip2region";

	/**
	 * ip2region.db 文件路径
	 */
	private String dbFileLocation = "classpath:ip2region/ip2region.xdb";

}
