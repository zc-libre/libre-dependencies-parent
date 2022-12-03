package com.libre.security.annotation;

import com.libre.security.component.ResourceServerAutoConfiguration;
import com.libre.security.component.ResourceServerConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.lang.annotation.*;

/**
 * @author lengleng
 * @date 2022-06-04
 * <p>
 * 资源服务注解
 */
@Documented
@Inherited
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@EnableMethodSecurity
@Import({ ResourceServerAutoConfiguration.class, ResourceServerConfiguration.class})
public @interface EnableResourceServer {

}
