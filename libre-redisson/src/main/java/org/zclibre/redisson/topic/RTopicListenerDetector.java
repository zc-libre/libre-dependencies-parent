package org.zclibre.redisson.topic;

import org.zclibre.redisson.common.RModule;
import org.zclibre.redisson.common.RedisNameResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RPatternTopic;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * Redisson 监听器
 *
 * @author Libre
 */
@Slf4j
@RequiredArgsConstructor
public class RTopicListenerDetector implements BeanPostProcessor {

	private final RedissonClient client;

	private final RedisNameResolver resolver;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> userClass = ClassUtils.getUserClass(bean);
		ReflectionUtils.doWithMethods(userClass, method -> {
			RTopicListener listener = AnnotationUtils.findAnnotation(method, RTopicListener.class);
			if (listener != null) {
				String name = listener.value();
				Assert.hasText(name, "@RTopicListener value must not be empty.");
				log.info("Found @RTopicListener on bean:{} method:{}", beanName, method);

				// 校验 method，method 入参数大于等于1
				int paramCount = method.getParameterCount();
				if (paramCount > 1) {
					throw new IllegalArgumentException(
							"@RTopicListener on method " + method + " parameter count must less or equal to 1.");
				}
				// 处理名称，统一前缀，避免冲突
				String topicName = resolver.resolve(RModule.Topic, name);
				// 精准模式和模糊匹配模式
				if (TopicUtil.isPattern(topicName)) {
					RTopic topic = client.getTopic(topicName);
					topic.addListener(Object.class, (channel, msg) -> invokeMethod(bean, method, paramCount,
							new RTopicEvent<>(null, channel, msg)));
				}
				else {
					RPatternTopic patternTopic = client.getPatternTopic(topicName);
					patternTopic.addListener(Object.class, (pattern, channel, msg) -> invokeMethod(bean, method,
							paramCount, new RTopicEvent<>(pattern, channel, msg)));
				}
			}
		}, ReflectionUtils.USER_DECLARED_METHODS);
		return bean;
	}

	private static void invokeMethod(Object bean, Method method, int paramCount, RTopicEvent<Object> topicEvent) {
		// 支持没有参数的方法
		if (paramCount == 0) {
			ReflectionUtils.invokeMethod(method, bean);
		}
		else {
			ReflectionUtils.invokeMethod(method, bean, topicEvent);
		}
	}

}
