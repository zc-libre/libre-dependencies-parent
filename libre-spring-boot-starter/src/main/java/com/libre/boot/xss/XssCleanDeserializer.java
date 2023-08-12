package com.libre.boot.xss;

import com.libre.boot.autoconfigure.SpringContext;
import com.libre.boot.autoconfigure.XssProperties;
import com.libre.boot.xss.XssCleaner;
import com.libre.boot.xss.XssUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * jackson xss 处理
 *
 * @author L.cm
 */
@Slf4j
public class XssCleanDeserializer extends XssCleanDeserializerBase {

	@Override
	public String clean(String name, String text) throws IOException {
		if (text == null) {
			return null;
		}
		// 读取 xss 配置
		XssProperties properties = SpringContext.getBean(XssProperties.class);
		if (properties == null) {
			return text;
		}
		// 读取 XssCleaner bean
		XssCleaner xssCleaner = SpringContext.getBean(XssCleaner.class);
		if (xssCleaner == null) {
			return XssUtil.trim(text, properties.isTrimText());
		}
		String value = xssCleaner.clean(name, XssUtil.trim(text, properties.isTrimText()), XssType.JACKSON);
		log.debug("Json property name:{} value:{} cleaned up by mica-xss, current value is:{}.", name, text, value);
		return value;
	}

}
