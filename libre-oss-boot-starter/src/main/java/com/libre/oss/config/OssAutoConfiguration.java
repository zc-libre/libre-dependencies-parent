package com.libre.oss.config;


import com.libre.oss.support.OssTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * oss 自动配置类
 *
 * @author lengleng
 * @author 858695266
 * @author L.cm
 */
@AutoConfiguration
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(prefix = OssProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class OssAutoConfiguration {

	/**
	 * OSS操作模板
	 * @return OSS操作模板
	 */
	@Bean
	public OssTemplate ossTemplate(OssProperties properties) {
		return new OssTemplate(properties);
	}

}

