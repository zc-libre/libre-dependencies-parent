package org.zclibre.toolkit.function;

import java.io.Serializable;

/**
 * 受检的 Consumer
 *
 * @author Libre
 */
@FunctionalInterface
public interface CheckedConsumer<T> extends Serializable {

	/**
	 * Run the Consumer
	 * @param t T
	 * @throws Throwable UncheckedException
	 */
	void accept(T t) throws Throwable;

}
