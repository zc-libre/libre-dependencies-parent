package com.libre.mybatis.annotation;

import java.lang.annotation.*;

/**
 * @author ZC
 * @date 2021/11/4 23:20
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DataScope {
	String type() default "";

	DataColumn[] value() default {};

	boolean ignore() default false;
}
