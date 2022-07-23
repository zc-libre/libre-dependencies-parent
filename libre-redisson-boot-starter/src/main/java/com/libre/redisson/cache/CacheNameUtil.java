package com.libre.redisson.cache;

import jodd.util.StringPool;
import lombok.experimental.UtilityClass;
import org.springframework.boot.convert.DurationStyle;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * cache name 处理
 *
 * @author Libre
 */
@UtilityClass
public class CacheNameUtil {

	/**
	 * 从 cache name 中解析 ttl，例如： user:test#300ms，不带单位默认为 s 秒
	 *
	 * @param cacheName 缓存名
	 * @return 超时时间
	 */
	public static long getTTLFormCacheName(String cacheName) {
		String[] cacheArray = cacheName.split(StringPool.HASH);
		if (cacheArray.length < 2) {
			return -1L;
		}
		Duration duration = DurationStyle.detectAndParse(cacheArray[1], ChronoUnit.SECONDS);
		return duration.toMillis();
	}
}
