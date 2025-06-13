package org.zclibre.redisson.command;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * Redisson
 *
 * @author Libre
 */
@AutoConfiguration
@ConditionalOnBean(RedissonClient.class)
@RequiredArgsConstructor
public class RedissonCommandConfiguration {

	private final RedissonClient client;

	@Bean
	public RedissonUtils redis() {
		return new RedissonUtils(client);
	}

}
