package com.libre.redisson.topic;

import java.lang.annotation.*;

/**
 * 基于 Redisson 的消息监听器
 *
 * @author Libre
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RTopicListener {

	/**
	 * topic name，支持通配符， 如 *、? 和 [...]
	 *
	 * @return String
	 */
	String value();

}
