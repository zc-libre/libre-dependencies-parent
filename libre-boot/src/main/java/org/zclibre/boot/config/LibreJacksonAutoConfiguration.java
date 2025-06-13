package com.libre.boot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.libre.toolkit.time.DatePattern;
import com.libre.toolkit.time.LocalDateTimeModule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author zhao.cheng
 * @date 2021/3/3 10:35
 */
@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
@AutoConfigureBefore(JacksonAutoConfiguration.class)
public class LibreJacksonAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public Jackson2ObjectMapperBuilderCustomizer customizer() {
		return builder -> {
			builder.locale(Locale.CHINA);
			builder.timeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
			builder.simpleDateFormat(DatePattern.NORM_DATETIME_PATTERN);
			builder.serializerByType(Long.class, ToStringSerializer.instance);
			builder.modules(LocalDateTimeModule.INSTANCE);
			builder.findModulesViaServiceLoader(true);
		};
	}

}
