package com.libre.redisson.topic;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 基于 Redisson 的事件对象
 *
 * @author Libre
 */
@Getter
@AllArgsConstructor
public class RTopicEvent<M> {

	/**
	 * 匹配模式时的正则
	 */
	private final CharSequence pattern;

	/**
	 * channel
	 */
	private final CharSequence channel;

	/**
	 * pub 的消息对象
	 */
	private final M msg;

}
