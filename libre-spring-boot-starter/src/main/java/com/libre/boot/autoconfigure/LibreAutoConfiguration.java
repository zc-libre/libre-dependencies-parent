package com.libre.boot.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Libre
 * @Date: 2022/6/16 12:12 AM
 */
@Configuration(proxyBeanMethods = false)
public class LibreAutoConfiguration {

	@Bean
	public SpringContext springContext() {
		return new SpringContext();
	}
}
