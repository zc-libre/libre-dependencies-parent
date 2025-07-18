package org.zclibre.redisson.spel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

/**
 * ExpressionRootObject
 *
 * @author L.cm
 */
@Getter
@RequiredArgsConstructor
public class CachedExpressionRootObject {

	private final Method method;

	private final Object[] args;

	private final Object target;

	private final Class<?> targetClass;

	private final Method targetMethod;

}
