package org.zclibre.ip2region.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ip2region 配置类
 *
 * @author L.cm
 */
@Getter
@Setter
@ConfigurationProperties(Ip2regionProperties.PREFIX)
public class Ip2regionProperties {

	public static final String PREFIX = "libre.ip2region";

	/**
	 * ip2region.db 文件路径
	 */
	private String dbFileLocation = "classpath:ip2region/ip2region.xdb";

	/**
	 * ipv6wry.db 文件路径
	 */
	private String ipv6dbFileLocation = "classpath:ip2region/ipv6wry.db";

}
