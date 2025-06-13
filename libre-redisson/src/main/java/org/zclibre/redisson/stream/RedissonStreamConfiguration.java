package org.zclibre.redisson.stream;

import org.zclibre.redisson.common.RedisNameResolver;
import org.zclibre.toolkit.constant.LibreConstants;
import org.zclibre.toolkit.core.StringUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Redisson 延迟队列配置
 *
 * @author Libre
 */
@AutoConfiguration
@ConditionalOnBean(RedissonClient.class)
@RequiredArgsConstructor
@EnableConfigurationProperties(RedissonStreamProperties.class)
public class RedissonStreamConfiguration {

	private final RedissonClient client;

	private final RedisNameResolver resolver;

	@Bean
	public RStreamSender streamSender() {
		return new RedissonStreamSender(client, resolver);
	}

	@Bean
	public RStreamListenerDetector streamListenerDetector(Environment environment,
			RedissonStreamProperties properties) {
		String groupId = properties.getConsumer().getGroupId();
		if (StringUtil.isBlank(groupId)) {
			groupId = environment.getRequiredProperty(LibreConstants.SPRING_APP_NAME_KEY);
		}
		return new RStreamListenerDetector(client, resolver, groupId);
	}

}
