package org.zclibre.redis.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * CacheManagerCustomizers配置
 *
 * @author zhao.cheng
 */
@AutoConfiguration
@ConditionalOnMissingBean(CacheManagerCustomizers.class)
public class RedisCacheManagerConfig {

	@Bean
	public CacheManagerCustomizers cacheManagerCustomizers(
			ObjectProvider<List<CacheManagerCustomizer<?>>> customizers) {
		return new CacheManagerCustomizers(customizers.getIfAvailable());
	}

}
