package com.libre.redisson.stream;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * redisson 缓存
 *
 * @author Libre
 */
@Getter
@Setter
@ConfigurationProperties("libre.redisson.stream")
public class RedissonStreamProperties {

	/**
	 * 消费者配置
	 */
	private final Consumer consumer = new Consumer();

	@Getter
	@Setter
	public static class Consumer {

		/**
		 * 消费者分组
		 */
		private String groupId;

	}

}
