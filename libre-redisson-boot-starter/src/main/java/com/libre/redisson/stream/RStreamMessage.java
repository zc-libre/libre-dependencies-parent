package com.libre.redisson.stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.redisson.api.StreamMessageId;

/**
 * redis stream 消息封装
 *
 * @param <K> key 类型
 * @param <V> value 类型
 * @author Libre
 */
@Getter
@RequiredArgsConstructor
public class RStreamMessage<K, V> {

	private final StreamMessageId messageId;

	private final K key;

	private final V value;

}
