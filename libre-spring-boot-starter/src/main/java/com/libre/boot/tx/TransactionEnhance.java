package com.libre.boot.tx;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 事务增强
 *
 * <p>
 * 参考：
 * https://github.com/spring-projects/spring-framework/blob/master/spring-context-support/src/main/java/org/springframework/cache/transaction/TransactionAwareCacheDecorator.java
 * </p>
 *
 * @author Libre
 */
public class TransactionEnhance {

	/**
	 * 判断当前调用是否在事务环境中，确保spring事务提交后执行，适用于commit后刷新缓存,发送消息等场景
	 *
	 * @param runnable Runnable
	 */
	public static void afterCommit(Runnable runnable) {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			// 事务提交后执行方法
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					runnable.run();
				}
			});
		} else {
			runnable.run();
		}
	}
}
