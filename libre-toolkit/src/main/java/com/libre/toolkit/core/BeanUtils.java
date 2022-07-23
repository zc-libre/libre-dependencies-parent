package com.libre.toolkit.core;

import lombok.experimental.UtilityClass;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanMap;
import net.sf.cglib.core.Converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author ZC
 * @date 2021/12/19 23:47
 */
@UtilityClass
public class BeanUtils {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> beanToMap(Object bean) {
		return null == bean ? null : BeanMap.create(bean);
	}

	public static <T> T copy(Object source, Class<T> targetClass) {
		return copy(source, targetClass, null);
	}

	public static <T> List<T> copy(Collection<?> sourceList, Class<T> targetClazz) {
		return copy(sourceList, null, targetClazz);
	}

	public static <T> T copy(Object source, Class<T> targetClass, Converter converter) {
		Objects.requireNonNull(targetClass);
		final T target = newInstance(targetClass);
		copy(source, target, converter);
		return target;
	}

	public static void copy(Object source, Object target) {
		copy(source, target, null);
	}

	public static void copy(Object source, Object target, Converter converter) {
		Objects.requireNonNull(source, "source object must not be null");
		BeanCopier beanCopier = BeanCopier.create(source.getClass(), target.getClass(), converter != null);
		beanCopier.copy(source, target, converter);
	}

	public static <T> T newInstance(Class<T> clazz) {
		Objects.requireNonNull(clazz, "class must not be null");
		if (clazz.isInterface()) {
			throw new IllegalArgumentException("Specified class [" + clazz.getName() + "] is an interface");
		}
		try {
			Constructor<T> constructor = clazz.getDeclaredConstructor();
			return constructor.newInstance();
		}
		catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			throw Exceptions.unchecked(e);
		}
	}

	public static <T> List<T> copy(Collection<?> sourceList, List<T> targetList, Class<T> targetClazz) {
		if (sourceList == null || sourceList.isEmpty()) {
			return Collections.emptyList();
		}
		if (targetList == null) {
			targetList = new ArrayList<>(sourceList.size());
		}
		for (Object source : sourceList) {
			if (source == null) {
				continue;
			}
			T bean = BeanUtils.copy(source, targetClazz);
			targetList.add(bean);
		}
		return targetList;
	}

}
