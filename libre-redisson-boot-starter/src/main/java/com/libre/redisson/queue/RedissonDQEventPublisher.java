package com.libre.redisson.queue;

import com.libre.redisson.common.RModule;
import com.libre.redisson.common.RedisNameResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;

/**
 * Redisson 延迟队列消息发布器
 *
 * @author Libre
 */
@Slf4j
@RequiredArgsConstructor
public class RedissonDQEventPublisher implements InitializingBean, RDQEventPublisher {

	private final RedissonClient client;

	private final RedisNameResolver resolver;

	@Override
	public <E> RDelayedQueue<E> getDelayedQueue(String name) {
		String queueName = resolver.resolve(RModule.DQueue, name);
		RBlockingQueue<E> queue = client.getBlockingQueue(queueName);
		// 使用延迟队列
		return client.getDelayedQueue(queue);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("RDQEventPublisher init success.");
	}

}
