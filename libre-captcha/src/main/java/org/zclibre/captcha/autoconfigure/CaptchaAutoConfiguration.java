package com.libre.captcha.autoconfigure;

import com.libre.captcha.cache.CaptchaCache;
import com.libre.captcha.service.CaptchaService;
import com.libre.captcha.service.CaptchaServiceImpl;
import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZC
 * @date 2021/7/17 0:56
 */
@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "libre.captcha", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(CaptchaProperties.class)
public class CaptchaAutoConfiguration {

	private final CaptchaProperties properties;

	@Bean
	@ConditionalOnProperty(prefix = "libre.captcha", name = "captcha-type", havingValue = "spec")
	public Captcha specCaptcha() {
		SpecCaptcha specCaptcha = new SpecCaptcha();
		setProperties(specCaptcha);
		return specCaptcha;
	}

	@Bean
	@ConditionalOnProperty(prefix = "libre.captcha", name = "captcha-type", matchIfMissing = true)
	public Captcha arithmeticCaptcha() {
		ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha();
		setProperties(arithmeticCaptcha);
		return arithmeticCaptcha;
	}

	@Bean
	@ConditionalOnProperty(prefix = "libre.captcha", name = "captcha-type", havingValue = "chinese_gif")
	public Captcha chineseGifCaptcha() {
		ChineseGifCaptcha chineseGifCaptcha = new ChineseGifCaptcha();
		setProperties(chineseGifCaptcha);
		return chineseGifCaptcha;
	}

	@Bean
	@ConditionalOnProperty(prefix = "libre.captcha", name = "captcha-type", havingValue = "chinese")
	public ChineseCaptcha chineseCaptcha() {
		ChineseCaptcha chineseCaptcha = new ChineseCaptcha();
		setProperties(chineseCaptcha);
		return chineseCaptcha;
	}

	@Bean
	@ConditionalOnProperty(prefix = "libre.captcha", name = "captcha-type", havingValue = "gif")
	public GifCaptcha gifCaptcha() {
		GifCaptcha gifCaptcha = new GifCaptcha();
		setProperties(gifCaptcha);
		return gifCaptcha;
	}

	@Bean
	@ConditionalOnMissingBean
	public CaptchaService captchaService(CaptchaCache captchaCache) {
		return new CaptchaServiceImpl(captchaCache, properties);
	}

	private void setProperties(Captcha captcha) {
		captcha.setWidth(properties.getWidth());
		captcha.setHeight(properties.getHeight());
		captcha.setLen(properties.getLength());
	}

}
