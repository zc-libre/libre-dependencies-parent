package org.zclibre.redisson.stream;

import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamAddArgs;

import java.util.Map;

/**
 * 基于 Redisson 的消息发布器
 *
 * @author Libre
 */
public interface RStreamSender {

	/**
	 * 发布消息
	 * @param name 队列名
	 * @param value 消息
	 * @return 消息id
	 */
	default StreamMessageId send(String name, Object value) {
		return send(name, null, value);
	}

	/**
	 * 发布消息
	 * @param name 队列名
	 * @param key 消息key
	 * @param value 消息
	 * @return 消息id
	 */
	default StreamMessageId send(String name, Object key, Object value) {
		return send(name, StreamAddArgs.entry(key, value));
	}

	/**
	 * 发布消息
	 * @param name 队列名
	 * @param addArgs StreamAddArgs
	 * @param <K> key type
	 * @param <V> value type
	 * @return 消息id
	 */
	<K, V> StreamMessageId send(String name, StreamAddArgs<K, V> addArgs);

	/**
	 * 批量发布
	 * @param name 队列名
	 * @param messages 消息
	 * @return 消息id
	 */
	default StreamMessageId sendAll(String name, Map<Object, Object> messages) {
		return send(name, StreamAddArgs.entries(messages));
	}

}
