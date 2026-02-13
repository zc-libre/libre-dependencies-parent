package org.zclibre.boot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.zclibre.toolkit.time.DatePattern;
import org.zclibre.toolkit.time.LocalDateTimeModule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
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
	public JsonMapperBuilderCustomizer customizer() {
		return builder -> {
			builder.defaultLocale(Locale.CHINA);
			builder.defaultTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
			builder.defaultDateFormat(new java.text.SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN));
			builder.findAndAddModules(LibreJacksonAutoConfiguration.class.getClassLoader());
		};
	}

}
