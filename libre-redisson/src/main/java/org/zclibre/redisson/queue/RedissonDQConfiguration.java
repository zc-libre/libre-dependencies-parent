package org.zclibre.redisson.queue;

import org.zclibre.redisson.common.RedisNameResolver;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * Redisson 延迟队列配置
 *
 * @author Libre
 */
@RequiredArgsConstructor
@AutoConfiguration
@ConditionalOnBean(RedissonClient.class)
public class RedissonDQConfiguration {

	private final RedissonClient client;

	private final RedisNameResolver resolver;

	@Bean
	public RDQEventPublisher rdqEventPublisher() {
		return new RedissonDQEventPublisher(client, resolver);
	}

	@Bean
	public RDQListenerDetector rdqListenerDetector() {
		return new RDQListenerDetector(client, resolver);
	}

}
