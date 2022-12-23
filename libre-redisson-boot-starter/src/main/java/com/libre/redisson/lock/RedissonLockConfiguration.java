package com.libre.redisson.lock;

import com.libre.redisson.common.RedisNameResolver;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 分布式锁自动化配置
 *
 * @author Libre
 */
@AutoConfiguration
@ConditionalOnBean(RedissonClient.class)
@RequiredArgsConstructor
@EnableConfigurationProperties(RedisLockProperties.class)
public class RedissonLockConfiguration {

	private final RedissonClient client;

	private final RedisNameResolver resolver;

	@Bean
	public RedisLockClient redisLockClient() {
		return new RedisLockClientImpl(client, resolver);
	}

	@Bean
	public RedisLockAspect redisLockAspect(RedisLockClient redisLockClient) {
		return new RedisLockAspect(redisLockClient);
	}

}
