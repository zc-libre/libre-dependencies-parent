package org.zclibre.toolkit.function;

import java.io.Serializable;

/**
 * 受检的 runnable
 *
 * @author Libre
 */
@FunctionalInterface
public interface CheckedRunnable extends Serializable {

	/**
	 * Run this runnable.
	 * @throws Throwable CheckedException
	 */
	void run() throws Throwable;

}
