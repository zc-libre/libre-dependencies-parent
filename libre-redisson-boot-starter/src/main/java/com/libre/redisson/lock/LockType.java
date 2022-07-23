package com.libre.redisson.lock;

/**
 * 锁类型
 *
 * @author lcm
 */
public enum LockType {
	/**
	 * 重入锁
	 */
	REENTRANT,
	/**
	 * 公平锁
	 */
	FAIR
}
