package com.libre.redisson.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 功能模块
 *
 * @author Libre
 */
@Getter
@RequiredArgsConstructor
public enum RModule {
	/**
	 * redis 命令 空间
	 */
	Command("command:"),
	/**
	 * 限流
	 */
	RateLimiter("rate-limiter:"),
	/**
	 * 分布式锁
	 */
	Locker("locker:"),
	/**
	 * 二级缓存
	 */
	LCache("local-cache:"),
	/**
	 * pub/sub
	 */
	Topic("topic:"),
	/**
	 * stream
	 */
	Stream("stream:"),
	/**
	 * 延迟队列
	 */
	DQueue("d-queue:");

	private final String prefix;
}
