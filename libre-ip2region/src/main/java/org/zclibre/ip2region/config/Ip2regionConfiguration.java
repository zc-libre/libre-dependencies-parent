package org.zclibre.ip2region.config;

import org.zclibre.ip2region.core.Ip2regionSearcher;
import org.zclibre.ip2region.impl.Ip2regionSearcherImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ResourceLoader;

/**
 * ip2region 自动化配置
 *
 * @author L.cm
 */
@AutoConfiguration
@ImportRuntimeHints(Ip2regionRuntimeHintsRegistrar.class)
@EnableConfigurationProperties(Ip2regionProperties.class)
public class Ip2regionConfiguration {

	@Bean
	public Ip2regionSearcher ip2regionSearcher(ResourceLoader resourceLoader, Ip2regionProperties properties) {
		return new Ip2regionSearcherImpl(resourceLoader, properties);
	}

}
