package com.libre.redisson.stream;

import com.libre.redisson.common.RModule;
import com.libre.redisson.common.RedisNameResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RStream;
import org.redisson.api.RedissonClient;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamAddArgs;
import org.springframework.beans.factory.InitializingBean;

/**
 * Redisson stream 发送数据
 *
 * @author Libre
 */
@Slf4j
@RequiredArgsConstructor
public class RedissonStreamSender implements InitializingBean, RStreamSender {

	private final RedissonClient client;

	private final RedisNameResolver resolver;

	@Override
	public <K, V> StreamMessageId send(String name, StreamAddArgs<K, V> addArgs) {
		String streamName = resolver.resolve(RModule.Stream, name);
		RStream<K, V> stream = client.getStream(streamName);
		return stream.add(addArgs);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("RStreamSender init success.");
	}

}
