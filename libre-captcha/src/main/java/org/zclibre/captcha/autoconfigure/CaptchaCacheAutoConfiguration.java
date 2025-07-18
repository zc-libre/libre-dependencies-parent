package org.zclibre.captcha.autoconfigure;

import org.zclibre.captcha.cache.CaptchaCache;
import org.zclibre.captcha.cache.SpringCacheCaptchaCache;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

/**
 * @author ZC
 * @date 2021/7/17 2:46
 */
@Order
@AutoConfiguration
@EnableConfigurationProperties(CaptchaProperties.class)
@RequiredArgsConstructor
public class CaptchaCacheAutoConfiguration {

	private final CacheManager cacheManager;

	@Bean
	@ConditionalOnMissingBean
	public CaptchaCache captchaCache(CaptchaProperties properties) {
		return new SpringCacheCaptchaCache(cacheManager, properties);
	}

}
