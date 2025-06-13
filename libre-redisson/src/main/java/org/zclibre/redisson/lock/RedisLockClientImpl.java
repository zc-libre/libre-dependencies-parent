package com.libre.redisson.lock;

import com.libre.redisson.common.RModule;
import com.libre.redisson.common.RedisNameResolver;
import org.zclibre.toolkit.core.Exceptions;
import org.zclibre.toolkit.function.CheckedSupplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * 锁客户端
 *
 * @author Libre
 */
@Slf4j
@RequiredArgsConstructor
public class RedisLockClientImpl implements RedisLockClient {

	private final RedissonClient redissonClient;

	private final RedisNameResolver resolver;

	@Override
	public boolean tryLock(String lockName, LockType lockType, long waitTime, long leaseTime, TimeUnit timeUnit)
			throws InterruptedException {
		RLock lock = getLock(lockName, lockType);
		return lock.tryLock(waitTime, leaseTime, timeUnit);
	}

	@Override
	public void unLock(String lockName, LockType lockType) {
		RLock lock = getLock(lockName, lockType);
		// 仅仅在已经锁定和当前线程持有锁时解锁
		if (lock.isLocked() && lock.isHeldByCurrentThread()) {
			lock.unlock();
		}
	}

	private RLock getLock(String name, LockType lockType) {
		String lockName = resolver.resolve(RModule.Locker, name);
		RLock lock;
		if (LockType.REENTRANT == lockType) {
			lock = redissonClient.getLock(lockName);
		}
		else {
			lock = redissonClient.getFairLock(lockName);
		}
		return lock;
	}

	@Override
	public <T> T lock(String lockName, LockType lockType, long waitTime, long leaseTime, TimeUnit timeUnit,
			CheckedSupplier<T> supplier) {
		try {
			if (tryLock(lockName, lockType, waitTime, leaseTime, timeUnit)) {
				return supplier.get();
			}
		}
		catch (Throwable e) {
			throw Exceptions.unchecked(e);
		}
		finally {
			this.unLock(lockName, lockType);
		}
		throw new RedisLockException(lockName, lockType, waitTime, leaseTime);
	}

}
