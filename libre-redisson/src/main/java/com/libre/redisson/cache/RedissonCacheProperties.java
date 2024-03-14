package com.libre.redisson.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * redisson 缓存
 *
 * @author Libre
 */
@Getter
@Setter

@ConfigurationProperties("libre.redisson.cache")
public class RedissonCacheProperties {

	/**
	 * 是否动态创建 cache，默认：true
	 */
	private boolean dynamic = true;

	/**
	 * 开启事务处理支持，会在事务执行完毕之后再处理缓存，默认：false
	 */
	private boolean enableTransactions = false;

	/**
	 * 允许 null 值，默认：true
	 */
	private boolean allowNullValues = true;

	/**
	 * 全局配置
	 */
	@NestedConfigurationProperty
	private RedissonCacheConfig global = new RedissonCacheConfig();

	/**
	 * 单个缓存的配置
	 */
	private Map<String, RedissonCacheConfig> config = new HashMap<>();

}
