package com.libre.boot.xss;

import com.libre.boot.autoconfigure.XssProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * jackson xss 处理
 *
 * @author L.cm
 */
@Slf4j
@RequiredArgsConstructor
public class JacksonXssClean extends XssCleanDeserializerBase {

	private final XssProperties properties;

	private final XssCleaner xssCleaner;

	@Override
	public String clean(String name, String text) throws IOException {
		if (text == null) {
			return null;
		}
		// 判断是否忽略
		if (XssHolder.isIgnore(name)) {
			return XssUtil.trim(text, properties.isTrimText());
		}
		String value = xssCleaner.clean(name, XssUtil.trim(text, properties.isTrimText()), XssType.JACKSON);
		log.debug("Json property name:{} value:{} cleaned up by mica-xss, current value is:{}.", name, text, value);
		return value;
	}

}
