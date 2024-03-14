package com.libre.boot.autoconfigure;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * @author zhao.cheng
 */
public class SpringContext implements ApplicationContextAware {

	@Nullable
	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		SpringContext.context = context;
	}

	@Nullable
	public static <T> T getBean(Class<T> clazz) {
		if (context == null) {
			return null;
		}
		return context.getBean(clazz);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName) {
		if (context == null) {
			return null;
		}
		return (T) context.getBean(beanName);
	}

	@Nullable
	public static <T> T getBean(String beanName, Class<T> clazz) {
		if (context == null) {
			return null;
		}
		return context.getBean(beanName, clazz);
	}

	@Nullable
	public static <T> ObjectProvider<T> getBeanProvider(Class<T> clazz) {
		if (context == null) {
			return null;
		}
		return context.getBeanProvider(clazz);
	}

	@Nullable
	public static <T> ObjectProvider<T> getBeanProvider(ResolvableType resolvableType) {
		if (context == null) {
			return null;
		}
		return context.getBeanProvider(resolvableType);
	}

	@Nullable
	public static ApplicationContext getContext() {
		return context;
	}

	public static void publishEvent(ApplicationEvent event) {
		if (context == null) {
			return;
		}
		context.publishEvent(event);
	}

	/**
	 * 获取aop代理对象
	 * @return 代理对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getCurrentProxy() {
		return (T) AopContext.currentProxy();
	}

}
