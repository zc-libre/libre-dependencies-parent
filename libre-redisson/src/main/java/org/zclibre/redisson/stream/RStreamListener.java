package org.zclibre.redisson.stream;

import java.lang.annotation.*;

/**
 * 基于 redis 的 stream 监听
 *
 * @author Libre
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RStreamListener {

	/**
	 * Queue name
	 * @return String
	 */
	String value();

}
