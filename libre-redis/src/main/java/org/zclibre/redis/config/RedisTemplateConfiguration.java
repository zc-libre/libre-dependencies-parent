package org.zclibre.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import org.zclibre.redis.cache.RedisUtils;
import org.zclibre.redis.serializer.ProtoStuffSerializer;
import org.zclibre.redis.serializer.RedisKeySerializer;
import org.zclibre.toolkit.time.LocalDateTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * RedisTemplate 配置
 *
 * @author zhao.cheng
 */
@Slf4j
@RequiredArgsConstructor
@AutoConfiguration
@AutoConfigureBefore(RedisAutoConfiguration.class)
@EnableConfigurationProperties(LibreRedisProperties.class)
public class RedisTemplateConfiguration {

	private final RedisConnectionFactory redisConnectionFactory;

	/**
	 * value 值 序列化
	 * @return RedisSerializer
	 */
	@Bean
	@ConditionalOnMissingBean(RedisSerializer.class)
	public RedisSerializer<Object> defaultRedisSerializer(LibreRedisProperties properties,
			ObjectProvider<ObjectMapper> objectProvider) {
		LibreRedisProperties.SerializerType serializerType = properties.getSerializerType();
		if (LibreRedisProperties.SerializerType.JDK == serializerType) {
			ClassLoader classLoader = this.getClass().getClassLoader();
			log.info("redis serializer-type: JDK");
			return new JdkSerializationRedisSerializer(classLoader);
		}
		if (LibreRedisProperties.SerializerType.PROTOSTUFF == serializerType) {
			return new ProtoStuffSerializer();
		}
		// jackson findAndRegisterModules，use copy
		ObjectMapper objectMapper = objectProvider.getIfAvailable(ObjectMapper::new).copy();

		// findAndRegisterModules
		objectMapper.findAndRegisterModules();
		objectMapper.registerModule(LocalDateTimeModule.INSTANCE);
		// class type info to json
		GenericJackson2JsonRedisSerializer.registerNullValueSerializer(objectMapper, null);
		objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), DefaultTyping.NON_FINAL,
				As.PROPERTY);
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		log.info("redis serializer-type: JSON");
		return new GenericJackson2JsonRedisSerializer(objectMapper);
	}

	@Bean(name = "redisTemplate")
	@ConditionalOnMissingBean(RedisTemplate.class)
	public RedisTemplate<String, Object> redisTemplate(RedisSerializer<Object> redisSerializer) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		// key 序列化
		RedisKeySerializer redisKeySerializer = new RedisKeySerializer();
		redisTemplate.setKeySerializer(redisKeySerializer);
		redisTemplate.setHashKeySerializer(redisKeySerializer);
		// value 序列化
		redisTemplate.setValueSerializer(redisSerializer);
		redisTemplate.setHashValueSerializer(redisSerializer);
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		return redisTemplate;
	}

	@Bean
	@ConditionalOnMissingBean(ValueOperations.class)
	public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
		return redisTemplate.opsForValue();
	}

	@Bean
	public RedisUtils redisUtils(RedisTemplate<String, Object> redisTemplate) {
		return new RedisUtils(redisTemplate);
	}

}
