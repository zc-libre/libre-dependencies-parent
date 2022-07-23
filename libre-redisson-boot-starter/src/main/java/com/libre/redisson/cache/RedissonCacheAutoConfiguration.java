package com.libre.redisson.cache;

import com.libre.redisson.common.RModule;
import com.libre.redisson.common.RedisNameResolver;
import lombok.RequiredArgsConstructor;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * redission 缓存自动配置
 *
 * @author Libre
 */
@EnableCaching
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@AutoConfigureBefore(CacheAutoConfiguration.class)
@EnableConfigurationProperties(RedissonCacheProperties.class)
public class RedissonCacheAutoConfiguration {

	private final RedissonClient redissonClient;

	private final RedisNameResolver redisNameResolver;

	@Bean
	public RedissonSpringLocalCachedCacheManager cacheManager(RedissonCacheProperties properties) {
		RedissonSpringLocalCachedCacheManager cacheManager = new RedissonSpringLocalCachedCacheManager(redissonClient,
				getCacheConfig(properties.getGlobal()), buildCacheConfigMap(properties.getConfig(), redisNameResolver));
		cacheManager.setDynamic(properties.isDynamic());
		cacheManager.setAllowNullValues(properties.isAllowNullValues());
		cacheManager.setTransactionAware(properties.isEnableTransactions());
		cacheManager.setRedisNameResolver(redisNameResolver);
		return cacheManager;
	}

	private static Map<String, LocalCachedMapOptions<Object, Object>> buildCacheConfigMap(
			Map<String, RedissonCacheConfig> config, RedisNameResolver cacheNameResolver) {
		if (config == null || config.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, LocalCachedMapOptions<Object, Object>> optionsMap = new HashMap<>();
		for (Map.Entry<String, RedissonCacheConfig> configEntry : config.entrySet()) {
			String cacheName = configEntry.getKey();
			// cache name 上有 ttl 时优先。
			long ttlFormCacheNameMillis = CacheNameUtil.getTTLFormCacheName(cacheName);
			if (cacheNameResolver != null) {
				cacheName = cacheNameResolver.resolve(RModule.LCache, cacheName);
			}
			LocalCachedMapOptions<Object, Object> cacheConfig = getCacheConfig(configEntry.getValue());
			if (ttlFormCacheNameMillis > 0) {
				cacheConfig.timeToLive(ttlFormCacheNameMillis);
			}
			optionsMap.put(cacheName, cacheConfig);
		}
		return optionsMap;
	}

	private static LocalCachedMapOptions<Object, Object> getCacheConfig(RedissonCacheConfig config) {
		return LocalCachedMapOptions.defaults().cacheSize(config.getMaxSize())
				.timeToLive(config.getTimeToLive().toMillis()).maxIdle(config.getMaxIdleTime().toMillis())
				.reconnectionStrategy(config.getReconnectionStrategy()).syncStrategy(config.getSyncStrategy())
				.evictionPolicy(config.getEvictionPolicy());
	}

}
