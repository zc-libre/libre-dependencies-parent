package org.zclibre.boot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.zclibre.toolkit.time.DatePattern;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.http.converter.autoconfigure.ServerHttpMessageConvertersCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.converter.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

/**
 * @author zhao.cheng
 * @date 2021/3/3 10:22
 */
@EnableWebMvc
@AutoConfiguration
@EnableConfigurationProperties({ UploadFileProperties.class })
@RequiredArgsConstructor
public class LibreWebMvcAutoConfiguration implements WebMvcConfigurer {

	private final ObjectMapper objectMapper;

	@Bean
	@ConditionalOnMissingBean
	public CorsFilter corsFilter() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
		corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
		corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
		corsConfiguration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(source);
	}

	@Bean
	public ServerHttpMessageConvertersCustomizer serverHttpMessageConvertersCustomizer() {
		return (builder) -> {
			builder.registerDefaults();
			builder.withStringConverter(new StringHttpMessageConverter(StandardCharsets.UTF_8));
			builder.withJsonConverter(new MappingJackson2HttpMessageConverter(objectMapper));
		};
	}

	@Override
	public void addFormatters(@NonNull FormatterRegistry registry) {
		DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
		registrar.setTimeFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN));
		registrar.setDateFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
		registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
		registrar.registerFormatters(registry);
	}

}
