package com.libre.redisson.config;

import com.libre.redisson.common.RedisNameResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.PlaceholdersResolver;

/**
 * 默认的 redis 命名空间隔离，支持占位符 ${xxx}
 *
 * @author Libre
 */
@RequiredArgsConstructor
public class DefaultRedisNameResolver implements RedisNameResolver {

	private final PlaceholdersResolver placeholdersResolver;

	@Override
	public String resolvePlaceholders(String value) {
		return (String) placeholdersResolver.resolvePlaceholders(value);
	}

}
