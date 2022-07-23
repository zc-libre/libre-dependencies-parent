package com.libre.redisson.client;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * 主从配置
 *
 * @author Libre
 */
@Getter
@Setter
public class MasterSlaveServersConfig extends BaseMasterSlaveServersConfig {

	/**
	 * RedissonUtils slave servers addresses
	 */
	private Set<String> slaveAddresses = new HashSet<>();

	/**
	 * RedissonUtils master server address
	 */
	private String masterAddress;

	/**
	 * Database index used for RedissonUtils connection
	 */
	private int database = 0;
}
