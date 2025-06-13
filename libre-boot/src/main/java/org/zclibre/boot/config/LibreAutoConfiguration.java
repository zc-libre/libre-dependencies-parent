package org.zclibre.boot.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author: Libre
 * @Date: 2022/6/16 12:12 AM
 */

@AutoConfiguration
public class LibreAutoConfiguration {

	@Bean
	public SpringContext springContext() {
		return new SpringContext();
	}

}
