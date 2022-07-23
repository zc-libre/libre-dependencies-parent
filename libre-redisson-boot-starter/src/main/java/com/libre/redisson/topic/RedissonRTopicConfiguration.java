package com.libre.redisson.topic;

import com.libre.redisson.common.RedisNameResolver;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson pub/sub 发布配置
 *
 * @author Libre
 */
@Configuration(proxyBeanMethods = false)
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
