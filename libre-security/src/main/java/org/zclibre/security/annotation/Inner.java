package org.zclibre.security.annotation;

import java.lang.annotation.*;

/**
 * 服务调用不鉴权注解
 *
 * @author lengleng
 * @date 2020-06-14
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inner {

	/**
	 * 是否AOP统一处理
	 * @return false, true
	 */
	boolean value() default true;

	/**
	 * 需要特殊判空的字段(预留)
	 * @return {}
	 */
	String[] field() default {};

}
