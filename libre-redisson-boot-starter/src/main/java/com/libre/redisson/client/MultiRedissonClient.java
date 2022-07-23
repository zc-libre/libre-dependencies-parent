package com.libre.redisson.client;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 多 RedissonClient，备用
 *
 * @author Libre
 */
public class MultiRedissonClient implements InitializingBean, DisposableBean {
	private final ConcurrentMap<String, RedissonClient> clientMap = new ConcurrentHashMap<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		clientMap.put("default", Redisson.create());
	}

	@Override
	public void destroy() throws Exception {
		clientMap.values().forEach(RedissonClient::shutdown);
	}

}
