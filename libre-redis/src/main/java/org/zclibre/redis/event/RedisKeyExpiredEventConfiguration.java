package org.zclibre.redis.event;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * redis key 失效事件
 *
 * @author Libre
 */
@AutoConfiguration
@ConditionalOnProperty(value = "libre.redis.key-expired-event.enable")
public class RedisKeyExpiredEventConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		return container;
	}

	@Bean
	@ConditionalOnMissingBean
	public KeyExpirationEventMessageListener keyExpirationEventMessageListener(
			RedisMessageListenerContainer listenerContainer) {
		return new KeyExpirationEventMessageListener(listenerContainer);
	}

}
