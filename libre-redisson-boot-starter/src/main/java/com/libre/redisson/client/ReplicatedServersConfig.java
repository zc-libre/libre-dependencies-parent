package com.libre.redisson.client;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 云托管模式
 *
 * @author Libre
 */
@Getter
@Setter
public class ReplicatedServersConfig extends BaseMasterSlaveServersConfig {

	/**
	 * Replication group node urls list
	 */
	private List<String> nodeAddresses = new ArrayList<>();

	/**
	 * Replication group scan interval in milliseconds
	 */
	private int scanInterval = 1000;

	/**
	 * Database index used for RedissonUtils connection
	 */
	private int database = 0;
}
