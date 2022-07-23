package com.libre.redisson.cache;

import com.libre.redisson.common.RModule;
import com.libre.redisson.common.RedisNameResolver;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonCache;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Redisson LocalMapCache for spring cache
 *
 * @author Libre
 */
public class RedissonSpringLocalCachedCacheManager extends AbstractTransactionSupportingCacheManager implements CacheManager {

	private boolean dynamic = true;
	private boolean allowNullValues = true;

	private final RedissonClient redisson;
	private final LocalCachedMapOptions<Object, Object> globalOptions;
	private final ConcurrentMap<String, LocalCachedMapOptions<Object, Object>> cacheConfigMap;
	private RedisNameResolver redisNameResolver;
	/**
	 * cache 实例
 	 */
	private final ConcurrentMap<String, Cache> instanceMap = new ConcurrentHashMap<>();

	public RedissonSpringLocalCachedCacheManager(RedissonClient redisson,
												 LocalCachedMapOptions<Object, Object> globalOptions,
												 Map<String, LocalCachedMapOptions<Object, Object>> cacheConfigMap) {
		this.redisson = redisson;
		this.globalOptions = globalOptions;
		this.cacheConfigMap = new ConcurrentHashMap<>(cacheConfigMap);
	}

	/**
	 * Defines possibility of storing {@code null} values.
	 * <p>
	 * Default is <code>true</code>
	 *
	 * @param allowNullValues - stores if <code>true</code>
	 */
	public void setAllowNullValues(boolean allowNullValues) {
		this.allowNullValues = allowNullValues;
	}

	/**
	 * 设置是否动态
	 *
	 * @param dynamic dynamic
	 */
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public void setRedisNameResolver(RedisNameResolver redisNameResolver) {
		this.redisNameResolver = redisNameResolver;
	}

	private LocalCachedMapOptions<Object, Object> createDefaultConfig(String cacheName) {
		LocalCachedMapOptions<Object, Object> cacheConfig = LocalCachedMapOptions.defaults()
			.timeToLive(globalOptions.getTimeToLiveInMillis())
			.maxIdle(globalOptions.getMaxIdleInMillis())
			.cacheSize(globalOptions.getCacheSize())
			.reconnectionStrategy(globalOptions.getReconnectionStrategy())
			.syncStrategy(globalOptions.getSyncStrategy())
			.evictionPolicy(globalOptions.getEvictionPolicy());
		// 从缓存名中动态解析 ttl
		long ttlFormCacheNameMillis = CacheNameUtil.getTTLFormCacheName(cacheName);
		if (ttlFormCacheNameMillis > 0) {
			cacheConfig.timeToLive(ttlFormCacheNameMillis);
		}
		return cacheConfig;
	}

	@Override
	protected Collection<? extends Cache> loadCaches() {
		List<Cache> caches = new LinkedList<>();
		for (Map.Entry<String, LocalCachedMapOptions<Object, Object>> entry : cacheConfigMap.entrySet()) {
			caches.add(createLocalMapCache(entry.getKey(), entry.getValue()));
		}
		return caches;
	}

	@Override
	public Cache getCache(String cacheName) {
		Cache cache = instanceMap.get(cacheName);
		if (cache != null) {
			return cache;
		}
		if (!dynamic) {
			return null;
		}
		// 缓存 key 处理
		if (redisNameResolver != null) {
			cacheName = redisNameResolver.resolve(RModule.LCache, cacheName);
		}
		LocalCachedMapOptions<Object, Object> mapOptions = cacheConfigMap.computeIfAbsent(cacheName, this::createDefaultConfig);
		// 如果为默认的配置参数都为 0，则直连 redis，不走本地
		if (mapOptions.getMaxIdleInMillis() == 0L
			&& mapOptions.getTimeToLiveInMillis() == 0L
			&& mapOptions.getCacheSize() == 0) {
			return createMap(cacheName);
		}
		return createLocalMapCache(cacheName, mapOptions);
	}

	private Cache createMap(String cacheName) {
		RMap<Object, Object> map = getMap(cacheName);
		Cache cache = new RedissonCache(map, allowNullValues);
		Cache oldCache = instanceMap.putIfAbsent(cacheName, cache);
		if (oldCache != null) {
			cache = oldCache;
		}
		return cache;
	}

	private RMap<Object, Object> getMap(String cacheName) {
		return redisson.getMap(cacheName);
	}

	private Cache createLocalMapCache(String cacheName, LocalCachedMapOptions<Object, Object> mapOptions) {
		RLocalCachedMap<Object, Object> map = getLocalMapCache(cacheName, mapOptions);
		Cache cache = new RedissonCache(map, allowNullValues);
		Cache oldCache = instanceMap.putIfAbsent(cacheName, cache);
		if (oldCache != null) {
			cache = oldCache;
		}
		return cache;
	}

	private RLocalCachedMap<Object, Object> getLocalMapCache(String cacheName, LocalCachedMapOptions<Object, Object> mapOptions) {
		return redisson.getLocalCachedMap(cacheName, mapOptions);
	}

	@Override
	public Collection<String> getCacheNames() {
		return Collections.unmodifiableSet(cacheConfigMap.keySet());
	}

}
