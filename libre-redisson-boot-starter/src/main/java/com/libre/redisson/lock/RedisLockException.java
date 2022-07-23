package com.libre.redisson.lock;

import lombok.Getter;

/**
 * redis lock 异常
 *
 * @author Libre
 */
@Getter
public class RedisLockException extends RuntimeException {
	private final String lockName;
	private final LockType lockType;
	private final long waitTime;
	private final long leaseTime;

	public RedisLockException(String lockName, LockType lockType, long waitTime, long leaseTime) {
		super(String.format("Failed to acquire distributed lock with name %s, lock type %s, wait time %d, lease time %d.", lockName, lockType, waitTime, leaseTime));
		this.lockName = lockName;
		this.lockType = lockType;
		this.waitTime = waitTime;
		this.leaseTime = leaseTime;
	}

}
