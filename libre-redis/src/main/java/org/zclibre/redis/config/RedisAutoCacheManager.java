package org.zclibre.redis.config;

import org.zclibre.toolkit.core.StringPool;
import org.zclibre.toolkit.core.StringUtil;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * redis cache 扩展cache name自动化配置
 *
 * @author zhao.cheng
 */
public class RedisAutoCacheManager extends RedisCacheManager {

	public RedisAutoCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration,
			Map<String, RedisCacheConfiguration> initialCacheConfigurations, boolean allowInFlightCacheCreation) {
		super(cacheWriter, defaultCacheConfiguration, allowInFlightCacheCreation, initialCacheConfigurations);
	}

	@Override
	protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
		if (StringUtil.isBlank(name) || !name.contains(StringPool.HASH)) {
			return super.createRedisCache(name, cacheConfig);
		}
		String[] cacheArray = name.split(StringPool.HASH);
		if (cacheArray.length < 2) {
			return super.createRedisCache(name, cacheConfig);
		}
		String cacheName = cacheArray[0];
		if (cacheConfig != null) {
			// 转换时间，支持时间单位例如：300ms，第二个参数是默认单位
			Duration duration = DurationStyle.detectAndParse(cacheArray[1], ChronoUnit.SECONDS);
			cacheConfig = cacheConfig.entryTtl(duration);
		}
		return super.createRedisCache(cacheName, cacheConfig);
	}

}
