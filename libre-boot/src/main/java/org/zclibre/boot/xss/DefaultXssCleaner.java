package com.libre.boot.xss;

import com.google.common.base.Charsets;
import com.libre.boot.config.XssProperties;
import com.libre.toolkit.core.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.springframework.web.util.HtmlUtils;

/**
 * 默认的 xss 清理器
 *
 * @author L.cm
 */
public class DefaultXssCleaner implements XssCleaner {

	private final XssProperties properties;

	public DefaultXssCleaner(XssProperties properties) {
		this.properties = properties;
	}

	private static Document.OutputSettings getOutputSettings(XssProperties properties) {
		return new Document.OutputSettings()
			// 2. 转义，没找到关闭的方法，目前这个规则最少
			.escapeMode(Entities.EscapeMode.xhtml)
			// 3. 保留换行
			.prettyPrint(properties.isPrettyPrint());
	}

	@Override
	public String clean(String name, String bodyHtml, XssType type) {
		// 1. 为空直接返回
		if (StringUtil.isBlank(bodyHtml)) {
			return bodyHtml;
		}
		XssProperties.Mode mode = properties.getMode();
		if (XssProperties.Mode.escape == mode) {
			// html 转义
			return HtmlUtils.htmlEscape(bodyHtml, Charsets.UTF_8.name());
		}
		else if (XssProperties.Mode.validate == mode) {
			// 校验
			if (Jsoup.isValid(bodyHtml, XssUtil.WHITE_LIST)) {
				return bodyHtml;
			}
			throw type.getXssException(name, bodyHtml, "Xss validate fail, input value:" + bodyHtml);
		}
		else {
			// 4. 清理后的 html
			String escapedHtml = Jsoup.clean(bodyHtml, "", XssUtil.WHITE_LIST, getOutputSettings(properties));
			if (properties.isEnableEscape()) {
				return escapedHtml;
			}
			// 5. 反转义
			return Entities.unescape(escapedHtml);
		}
	}

}
