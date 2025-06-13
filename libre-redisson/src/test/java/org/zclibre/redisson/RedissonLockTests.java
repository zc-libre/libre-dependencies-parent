package com.libre.redisson;

import com.libre.redisson.lock.RedisLock;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ZC
 * @date 2022/2/5 23:54
 */
@SpringBootTest
public class RedissonLockTests {

	@Test
	void lock() {
		for (int i = 0; i < 10; i++) {
			test("lock");
		}
	}

	@RedisLock(value = "redis", param = "#val", leaseTime = 10000)
	private void test(String val) {
		System.out.println("lock...............");
	}

}
