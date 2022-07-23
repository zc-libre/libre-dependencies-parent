package com.libre.redisson.topic;

/**
 * 基于 Redisson 的消息发布器
 *
 * @author Libre
 */
public interface RTopicEventPublisher {

	/**
	 * 发布消息
	 *
	 * @param name 队列名
	 * @param msg  消息
	 * @return 收到消息的客户数量
	 */
	long publish(String name, Object msg);

}
