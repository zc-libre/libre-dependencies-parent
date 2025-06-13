package org.zclibre.redisson.topic;

import org.zclibre.redisson.common.RModule;
import org.zclibre.redisson.common.RedisNameResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;

/**
 * Redisson pub/sub 发布器
 *
 * @author Libre
 */
@Slf4j
@RequiredArgsConstructor
public class RedissonTopicEventPublisher implements InitializingBean, RTopicEventPublisher {

	private final RedissonClient client;

	private final RedisNameResolver resolver;

	@Override
	public long publish(String name, Object message) {
		String topicName = resolver.resolve(RModule.Topic, name);
		RTopic topic = client.getTopic(topicName);
		return topic.publish(message);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("RTopicEventPublisher init success.");
	}

}
