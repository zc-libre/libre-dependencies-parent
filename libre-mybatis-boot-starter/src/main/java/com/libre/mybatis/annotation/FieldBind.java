package com.libre.mybatis.annotation;

import java.lang.annotation.*;

/**
 * @author ZC
 * @date 2021/10/31 23:16
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
public @interface FieldBind {

	String type();

	String target();

}
