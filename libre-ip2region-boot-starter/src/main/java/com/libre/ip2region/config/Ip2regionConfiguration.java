package com.libre.ip2region.config;

import com.libre.ip2region.core.Ip2regionSearcher;
import com.libre.ip2region.impl.Ip2regionSearcherImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;


/**
 * ip2region 自动化配置
 *
 * @author L.cm
 */
@AutoConfiguration
@EnableConfigurationProperties(Ip2regionProperties.class)
public class Ip2regionConfiguration {

	@Bean
	public Ip2regionSearcher ip2regionSearcher(ResourceLoader resourceLoader,
											   Ip2regionProperties properties) {
		return new Ip2regionSearcherImpl(resourceLoader, properties);
	}

}
