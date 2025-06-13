package org.zclibre.toolkit.function;

import java.io.Serializable;

/**
 * 受检的 Supplier
 *
 * @author Libre
 */
@FunctionalInterface
public interface CheckedSupplier<T> extends Serializable {

	/**
	 * Run the Supplier
	 * @return T
	 * @throws Throwable CheckedException
	 */
	T get() throws Throwable;

}
