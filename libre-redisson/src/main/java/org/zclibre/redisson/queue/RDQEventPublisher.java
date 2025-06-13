package org.zclibre.redisson.queue;

import org.redisson.api.RDelayedQueue;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * 基于 Redisson 的消息发布器
 *
 * @author Libre
 */
public interface RDQEventPublisher {

	/**
	 * 获取延迟队列
	 * @param name 队列名
	 * @return 延迟队列
	 */
	<E> RDelayedQueue<E> getDelayedQueue(String name);

	/**
	 * 延迟发布消息
	 * @param name 队列名
	 * @param message 消息
	 * @param delay 延迟
	 * @param delayN 多参数
	 */
	default void publish(String name, Object message, Duration delay, Duration... delayN) {
		// 获取延迟队列
		RDelayedQueue<Object> delayedQueue = getDelayedQueue(name);
		delayedQueue.offer(message, delay.toMillis(), TimeUnit.MILLISECONDS);
		// 多参数的设定
		for (Duration duration : delayN) {
			delayedQueue.offer(message, duration.toMillis(), TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * 删除延迟发布消息
	 * @param name 队列名
	 * @param message 消息
	 */
	default void remove(String name, Object message) {
		// 获取延迟队列
		RDelayedQueue<Object> delayedQueue = getDelayedQueue(name);
		delayedQueue.remove(message);
	}

	/**
	 * 删除延迟发布消息
	 * @param name 队列名
	 * @param messages 消息集合
	 */
	default void removeAll(String name, Collection<Object> messages) {
		// 获取延迟队列
		RDelayedQueue<Object> delayedQueue = getDelayedQueue(name);
		delayedQueue.removeAll(messages);
	}

	/**
	 * 删除延迟发布消息
	 * @param name 队列名
	 * @param filter 过滤
	 */
	default <E> void removeIf(String name, Predicate<E> filter) {
		// 获取延迟队列
		RDelayedQueue<E> delayedQueue = getDelayedQueue(name);
		delayedQueue.removeIf(filter);
	}

}
