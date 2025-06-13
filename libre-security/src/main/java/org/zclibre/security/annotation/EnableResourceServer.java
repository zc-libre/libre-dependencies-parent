package org.zclibre.security.annotation;

import org.zclibre.security.component.Oauth2ResourceServerAutoConfiguration;
import org.zclibre.security.component.Oauth2ResourceServerConfiguration;
import org.springframework.context.annotation.Import;
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
@Import({ Oauth2ResourceServerAutoConfiguration.class, Oauth2ResourceServerConfiguration.class })
public @interface EnableResourceServer {

}
