package org.zclibre.redisson.queue;

import java.lang.annotation.*;

/**
 * 基于 Redisson 延迟队列监听器
 *
 * @author Libre
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RDQListener {

	/**
	 * Queue name
	 * @return String
	 */
	String value();

}
