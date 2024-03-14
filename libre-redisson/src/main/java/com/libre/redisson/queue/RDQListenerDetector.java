package com.libre.redisson.queue;

import com.libre.redisson.common.RModule;
import com.libre.redisson.common.RedisNameResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Redisson 监听器
 *
 * @author Libre
 */
@Slf4j
@RequiredArgsConstructor
public class RDQListenerDetector implements BeanPostProcessor {

	private final RedissonClient client;

	private final RedisNameResolver resolver;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> userClass = ClassUtils.getUserClass(bean);
		ReflectionUtils.doWithMethods(userClass, method -> {
			RDQListener listener = AnnotationUtils.findAnnotation(method, RDQListener.class);
			if (listener != null) {
				String name = listener.value();
				Assert.hasText(name, "@RDQListener value must not be empty.");
				log.info("Found @RDQListener on bean:{} method:{}", beanName, method);

				// 校验 method，method 入参数大于等于1
				int parameterCount = method.getParameterCount();
				if (parameterCount > 1) {
					throw new IllegalArgumentException(
							"@RDQListener on method " + method + " parameter count must less or equal to 1.");
				}
				// 处理名称，统一前缀，避免冲突
				String queueName = resolver.resolve(RModule.DQueue, name);
				RBlockingQueue<Object> blockingDeque = client.getBlockingQueue(queueName);
				// 初始化 DelayedQueue see: https://github.com/redisson/redisson/issues/2432
				client.getDelayedQueue(blockingDeque);
				// 注册监听器
				blockingDeque.subscribeOnElements(object -> ReflectionUtils.invokeMethod(method, bean, object));
			}
		}, ReflectionUtils.USER_DECLARED_METHODS);
		return bean;
	}

}
