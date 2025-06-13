package org.zclibre.redisson.client;

import lombok.Getter;
import lombok.Setter;

/**
 * 单机模式
 *
 * @author Libre
 */
@Getter
@Setter
public class SingleServerConfig extends BaseConfig {

	/**
	 * RedissonUtils server address
	 */
	private String address;

	/**
	 * Minimum idle subscription connection amount
	 */
	private int subscriptionConnectionMinimumIdleSize = 1;

	/**
	 * RedissonUtils subscription connection maximum pool size
	 */
	private int subscriptionConnectionPoolSize = 50;

	/**
	 * Minimum idle RedissonUtils connection amount
	 */
	private int connectionMinimumIdleSize = 24;

	/**
	 * RedissonUtils connection maximum pool size
	 */
	private int connectionPoolSize = 64;

	/**
	 * Database index used for RedissonUtils connection
	 */
	private int database = 0;

	/**
	 * Interval in milliseconds to check DNS
	 */
	private long dnsMonitoringInterval = 5000;

}
