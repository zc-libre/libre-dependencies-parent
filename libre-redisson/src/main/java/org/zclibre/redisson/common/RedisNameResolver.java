package org.zclibre.redisson.common;

/**
 * redis 命名空间隔离
 *
 * @author Libre
 */
public interface RedisNameResolver {

	/**
	 * 用于自定义“租户”等隔离，默认直接拼接
	 * @param module 模块名称
	 * @param name 名称
	 * @return 处理后的名称
	 */
	default String resolve(RModule module, String name) {
		return module.getPrefix().concat(resolvePlaceholders(name));
	}

	/**
	 * 处理占位符
	 * @param value 占位符
	 * @return 处理后的字符串
	 */
	default String resolvePlaceholders(String value) {
		return value;
	}

}
