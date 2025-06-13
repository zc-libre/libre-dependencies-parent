package org.zclibre.redisson.topic;

import org.zclibre.redisson.common.RedisNameResolver;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * Redisson pub/sub 发布配置
 *
 * @author Libre
 */
@AutoConfiguration
@ConditionalOnBean(RedissonClient.class)
@RequiredArgsConstructor
public class RedissonRTopicConfiguration {

	private final RedissonClient client;

	private final RedisNameResolver resolver;

	@Bean
	public RTopicEventPublisher topicEventPublisher() {
		return new RedissonTopicEventPublisher(client, resolver);
	}

	@Bean
	public RTopicListenerDetector topicListenerDetector() {
		return new RTopicListenerDetector(client, resolver);
	}

}
