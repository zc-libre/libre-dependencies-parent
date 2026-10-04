package org.zclibre.redisson;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.redisson.api.stream.StreamAddArgs;
import org.zclibre.redisson.client.RedissonProperties;
import org.zclibre.redisson.client.SingleServerConfig;
import org.zclibre.redisson.command.RedissonUtils;
import org.zclibre.redisson.common.RModule;
import org.zclibre.redisson.common.RedisNameResolver;
import org.zclibre.redisson.config.RedissonClientConfiguration;
import org.zclibre.redisson.lock.LockType;
import org.zclibre.redisson.lock.RedisLockClientImpl;
import org.zclibre.redisson.queue.RDQListener;
import org.zclibre.redisson.queue.RDQListenerDetector;
import org.zclibre.redisson.queue.RedissonDQEventPublisher;
import org.zclibre.redisson.stream.RStreamListener;
import org.zclibre.redisson.stream.RStreamListenerDetector;
import org.zclibre.redisson.stream.RStreamMessage;
import org.zclibre.redisson.stream.RedissonStreamSender;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 使用独立 Redis 实例验证升级兼容性，运行时指定 test.redis.address。
 *
 * @author Libre
 */
public class RedissonCompatibilityIT {

	private static final int WAIT_SECONDS = 10;

	private RedissonClient client;

	private RedisNameResolver resolver;

	@BeforeEach
	void connect() {
		String address = Objects.requireNonNull(System.getProperty("test.redis.address"), "请指定 test.redis.address");
		SingleServerConfig single = new SingleServerConfig();
		single.setAddress(address);
		RedissonProperties properties = new RedissonProperties();
		properties.setSingle(single);
		RedissonClientConfiguration configuration = new RedissonClientConfiguration();
		client = configuration.redissonClient(configuration.config(properties));
		String prefix = UUID.randomUUID() + ":";
		resolver = new RedisNameResolver() {
			@Override
			public String resolvePlaceholders(String value) {
				return prefix + value;
			}
		};
	}

	@AfterEach
	void disconnect() {
		if (client != null) {
			client.shutdown();
		}
	}

	@Test
	void roundTripsValuesAndExecutesNumericCommands() {
		RedissonUtils commands = new RedissonUtils(client);
		String key = resolver.resolvePlaceholders("value");
		Map<String, String> value = Map.of("message", "中文消息");
		commands.set(key, value);
		assertEquals(value, commands.get(key));
		assertEquals(-2L, commands.decrBy(resolver.resolvePlaceholders("counter"), 2L));
		assertTrue(commands.del(key));
	}

	@Test
	void acquiresAndReleasesLock() throws InterruptedException {
		RedisLockClientImpl locks = new RedisLockClientImpl(client, resolver);
		assertTrue(locks.tryLock("lock", LockType.REENTRANT, 0, WAIT_SECONDS, TimeUnit.SECONDS));
		assertTrue(client.getLock(resolver.resolve(RModule.Locker, "lock")).isHeldByCurrentThread());
		locks.unLock("lock", LockType.REENTRANT);
		assertFalse(client.getLock(resolver.resolve(RModule.Locker, "lock")).isLocked());
	}

	@Test
	void deliversDelayedMessageToAnnotatedListener() throws InterruptedException {
		MessageListener listener = new MessageListener();
		new RDQListenerDetector(client, resolver).postProcessAfterInitialization(listener, "messages");
		new RedissonDQEventPublisher(client, resolver).getDelayedQueue("delayed")
			.offer("delayed-message", 1, TimeUnit.MILLISECONDS);
		assertEquals("delayed-message", listener.delayed.poll(WAIT_SECONDS, TimeUnit.SECONDS));
	}

	@Test
	void deliversStreamMessageToAnnotatedListener() throws InterruptedException {
		RedissonStreamSender sender = new RedissonStreamSender(client, resolver);
		sender.send("stream", StreamAddArgs.entry("seed", "seed"));
		MessageListener listener = new MessageListener();
		new RStreamListenerDetector(client, resolver, "compatibility").postProcessAfterInitialization(listener,
				"messages");
		sender.send("stream", StreamAddArgs.entry("key", "stream-message"));
		assertEquals("stream-message", listener.stream.poll(WAIT_SECONDS, TimeUnit.SECONDS));
	}

	/**
	 * 真实消息回调。
	 *
	 * @author Libre
	 */
	public static class MessageListener {

		private final BlockingQueue<String> delayed = new LinkedBlockingQueue<>();

		private final BlockingQueue<String> stream = new LinkedBlockingQueue<>();

		@RDQListener("delayed")
		public void onDelayedMessage(String message) {
			delayed.add(message);
		}

		@RStreamListener("stream")
		public void onStreamMessage(RStreamMessage<String, String> message) {
			stream.add(message.getValue());
		}

	}

}
