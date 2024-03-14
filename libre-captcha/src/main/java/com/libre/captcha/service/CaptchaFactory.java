package com.libre.captcha.service;

import com.libre.captcha.autoconfigure.CaptchaProperties;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.experimental.UtilityClass;

/**
 * @author ZC
 * @date 2021/11/7 1:14
 */
@UtilityClass
public class CaptchaFactory {

	public static Captcha buildCaptcha(CaptchaProperties properties) {
		Captcha captcha;
		switch (properties.getCaptchaType()) {
		case GIF:
			captcha = new GifCaptcha();
			setProperties(captcha, properties);
			break;
		case SPEC:
			captcha = new SpecCaptcha();
			setProperties(captcha, properties);
			break;
		case CHINESE:
			captcha = new ChineseCaptcha();
			setProperties(captcha, properties);
			break;
		default:
			captcha = new ArithmeticCaptcha();
			setProperties(captcha, properties);
			break;
		}
		return captcha;
	}

	private static void setProperties(Captcha captcha, CaptchaProperties properties) {
		captcha.setWidth(properties.getWidth());
		captcha.setHeight(properties.getHeight());
		captcha.setLen(properties.getLength());
	}

}
