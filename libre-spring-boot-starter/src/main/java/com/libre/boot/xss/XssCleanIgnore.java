package com.libre.boot.xss;

import java.lang.annotation.*;

/**
 * 忽略 xss
 *
 * @author Libre
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XssCleanIgnore {
}
