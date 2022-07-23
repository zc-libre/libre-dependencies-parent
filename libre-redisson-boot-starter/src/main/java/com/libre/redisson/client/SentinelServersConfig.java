package com.libre.redisson.client;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 哨兵模式配置
 *
 * @author Libre
 */
@Getter
@Setter
public class SentinelServersConfig extends BaseMasterSlaveServersConfig {

	private List<String> sentinelAddresses = new ArrayList<>();

	private Map<String, String> natMap = Collections.emptyMap();

	private String masterName;

	/**
	 * Database index used for RedissonUtils connection
	 */
	private int database = 0;

	/**
	 * Sentinel scan interval in milliseconds
	 */
	private int scanInterval = 1000;
}
