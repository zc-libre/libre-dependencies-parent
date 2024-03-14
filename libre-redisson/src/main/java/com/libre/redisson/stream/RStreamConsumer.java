package com.libre.redisson.stream;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Consumer;

/**
 * redisson stream 消费者
 *
 * @author Libre
 */
@RequiredArgsConstructor
public class RStreamConsumer implements Consumer<Map<StreamMessageId, Map<Object, Object>>> {

	private final RStream<Object, Object> stream;

	private final String groupId;

	private final Object springBean;

	private final Method method;

	private final int paramCount;

	@Override
	public void accept(Map<StreamMessageId, Map<Object, Object>> messages) {
		messages.forEach((msgId, values) -> {
			values.forEach((key, value) -> invokeMethod(springBean, method, paramCount,
					new RStreamMessage<>(msgId, key, value)));
			// 如果成功，则回 ack，异常此处不会执行
			stream.ack(groupId, msgId);
		});
	}

	private static void invokeMethod(Object bean, Method method, int paramCount, Object streamMessage) {
		// 支持没有参数的方法
		if (paramCount == 0) {
			ReflectionUtils.invokeMethod(method, bean);
		}
		else {
			ReflectionUtils.invokeMethod(method, bean, streamMessage);
		}
	}

}
