package org.zclibre.toolkit.function;

import java.io.Serializable;

/**
 * 受检的 Callable
 *
 * @author Libre
 */
@FunctionalInterface
public interface CheckedCallable<T> extends Serializable {

	/**
	 * Run this callable.
	 * @return result
	 * @throws Throwable CheckedException
	 */
	T call() throws Throwable;

}
