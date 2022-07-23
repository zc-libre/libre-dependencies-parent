package com.libre.mybatis.annotation;

import java.lang.annotation.*;

/**
 * @author Libre
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Repeatable(DataScope.class)
public @interface DataColumn {

    String alias() default "";

    String name();
}
