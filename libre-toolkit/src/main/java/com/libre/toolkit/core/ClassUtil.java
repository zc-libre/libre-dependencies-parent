package com.libre.toolkit.core;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author ZC
 * @date 2022/3/13 0:26
 */
@UtilityClass
public class ClassUtil extends ClassUtils {

	/**
	 * 获取Annotation
	 * @param method Method
	 * @param annotationType 注解类
	 * @param <A> 泛型标记
	 * @return {Annotation}
	 */

	public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
		return MethodUtils.getAnnotation(method, annotationType, true, true);
	}

}
