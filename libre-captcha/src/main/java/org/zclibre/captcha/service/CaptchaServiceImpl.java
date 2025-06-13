package org.zclibre.captcha.service;

import org.zclibre.captcha.autoconfigure.CaptchaProperties;
import org.zclibre.captcha.cache.CaptchaCache;
import org.zclibre.captcha.vo.CaptchaVO;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * @author ZC
 * @date 2021/7/17 1:49
 */
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

	private final CaptchaCache captchaCache;

	private final CaptchaProperties properties;

	private Captcha captcha;

	@Override
	public void generate(String uuid) {
		captcha = CaptchaFactory.buildCaptcha(properties);
		String text = this.captcha.text();
		captchaCache.put(properties.getCacheName(), uuid, text);
	}

	@Override
	public CaptchaVO generateBase64Vo(String uuid) {
		generate(uuid);
		return new CaptchaVO(uuid, captcha.toBase64());
	}

	@Override
	public boolean isExpired(String uuid) {
		String text = captchaCache.get(uuid);
		return !StringUtils.hasText(text);
	}

	@Override
	public boolean validate(String uuid, String userInputCaptcha) {
		String code = captchaCache.getAndRemove(properties.getCacheName(), uuid);
		if (!StringUtils.hasText(code)) {
			return false;
		}
		return code.equalsIgnoreCase(userInputCaptcha);
	}

}
