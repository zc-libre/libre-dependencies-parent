package org.zclibre.redisson.config;

import org.zclibre.redisson.common.RedisNameResolver;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * redisson 配置
 *
 * @author Libre
 */
@AutoConfiguration
public class RedissonConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public RedissonClient redissonClient() {
		return Redisson.create();
	}

	@Bean
	@ConditionalOnMissingBean
	public RedisNameResolver redisNameResolver(Environment environment) {
		return new DefaultRedisNameResolver(new PropertySourcesPlaceholdersResolver(environment));
	}

}
