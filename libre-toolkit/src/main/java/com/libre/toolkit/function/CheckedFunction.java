package com.libre.toolkit.function;

import java.io.Serializable;

/**
 * 受检的 function
 *
 * @author Libre
 */
@FunctionalInterface
public interface CheckedFunction<T, R> extends Serializable {

	/**
	 * Run the Function
	 *
	 * @param t T
	 * @return R R
	 * @throws Throwable CheckedException
	 */
	R apply(T t) throws Throwable;

}
