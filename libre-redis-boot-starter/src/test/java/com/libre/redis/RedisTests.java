package com.libre.redis;

import com.libre.redis.cache.RedisUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author ZC
 * @date 2022/2/4 22:18
 */
@SpringBootTest
public class RedisTests {

	@Autowired
	RedisUtils redisUtils;

	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	@Test
	void set() {

	}

}
