package com.libre.boot.xss;

import com.google.common.base.Charsets;
import com.libre.boot.autoconfigure.XssProperties;
import com.libre.toolkit.core.StringUtil;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Cleaner;
import org.springframework.web.util.HtmlUtils;

/**
 * 默认的 xss 清理器
 *
 * @author L.cm
 */
@RequiredArgsConstructor
public class DefaultXssCleaner implements XssCleaner {

	private final XssProperties properties;

	@Override
	public String clean(String bodyHtml) {
		// 1. 为空直接返回
		if (StringUtil.isBlank(bodyHtml)) {
			return bodyHtml;
		}
		XssProperties.Mode mode = properties.getMode();
		if (XssProperties.Mode.escape == mode) {
			// html 转义
			return HtmlUtils.htmlEscape(bodyHtml, Charsets.UTF_8.name());
		}
		else {
			// jsoup html 清理
			Document.OutputSettings outputSettings = new Document.OutputSettings()
					// 2. 转义，没找到关闭的方法，目前这个规则最少
					.escapeMode(Entities.EscapeMode.xhtml)
					// 3. 保留换行
					.prettyPrint(properties.isPrettyPrint());
			Document dirty = Jsoup.parseBodyFragment(bodyHtml, "");
			Cleaner cleaner = new Cleaner(XssUtil.WHITE_LIST);
			Document clean = cleaner.clean(dirty);
			clean.outputSettings(outputSettings);
			// 4. 清理后的 html
			String escapedHtml = clean.body().html();
			if (properties.isEnableEscape()) {
				return escapedHtml;
			}
			// 5. 反转义
			return Entities.unescape(escapedHtml);
		}
	}

}
