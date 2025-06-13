package org.zclibre.redisson.client;

import lombok.Getter;
import lombok.Setter;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;

/**
 * 主从配置基础类
 *
 * @author Libre
 */
@Getter
@Setter
public class BaseMasterSlaveServersConfig extends BaseConfig {

	/**
	 * Сonnection load balancer for multiple RedissonUtils slave servers
	 */
	private LoadBalancerType loadBalancer = LoadBalancerType.ROUND_ROBIN;

	/**
	 * RedissonUtils 'slave' node minimum idle connection amount for <b>each</b> slave
	 * node
	 */
	private int slaveConnectionMinimumIdleSize = 24;

	/**
	 * RedissonUtils 'slave' node maximum connection pool size for <b>each</b> slave node
	 */
	private int slaveConnectionPoolSize = 64;

	private int failedSlaveReconnectionInterval = 3000;

	private int failedSlaveCheckInterval = 180000;

	/**
	 * RedissonUtils 'master' node minimum idle connection amount for <b>each</b> slave
	 * node
	 */
	private int masterConnectionMinimumIdleSize = 24;

	/**
	 * RedissonUtils 'master' node maximum connection pool size
	 */
	private int masterConnectionPoolSize = 64;

	private ReadMode readMode = ReadMode.SLAVE;

	private SubscriptionMode subscriptionMode = SubscriptionMode.MASTER;

	/**
	 * RedissonUtils 'slave' node minimum idle subscription (pub/sub) connection amount
	 * for <b>each</b> slave node
	 */
	private int subscriptionConnectionMinimumIdleSize = 1;

	/**
	 * RedissonUtils 'slave' node maximum subscription (pub/sub) connection pool size for
	 * <b>each</b> slave node
	 */
	private int subscriptionConnectionPoolSize = 50;

	private long dnsMonitoringInterval = 5000;

}
