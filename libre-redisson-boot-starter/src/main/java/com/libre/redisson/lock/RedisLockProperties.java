package com.libre.redisson.lock;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * 分布式锁配置
 *
 * @author Libre
 */
@Getter
@Setter
@ConfigurationProperties(RedisLockProperties.PREFIX)
public class RedisLockProperties {
	public static final String PREFIX = "libre.redisson.lock";

	/**
	 * 是否开启：默认为：true，便于生成配置提示。
	 */
	private Boolean enabled = Boolean.TRUE;

}
