package com.libre.boot.xss;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;

/**
 * xss clean
 *
 * <p>
 * 参考自 jpress：https://gitee.com/fuhai/jpress
 * </p>
 *
 * @author L.cm
 * @author michael
 */
public class XssUtil {

	public static final HtmlSafeList WHITE_LIST = HtmlSafeList.INSTANCE;

	/**
	 * trim 字符串
	 * @param text text
	 * @return 清理后的 text
	 */
	public static String trim(String text, boolean trim) {
		return trim ? text.strip() : text;
	}

	/**
	 * xss 清理
	 * @param html html
	 * @return 清理后的 html
	 */
	public static String clean(String html) {
		if (StringUtils.hasText(html)) {
			return Jsoup.clean(html, WHITE_LIST);
		}
		return html;
	}

	public static <A extends Annotation> A getAnnotation(HandlerMethod handlerMethod, Class<A> annotationType) {
		// 先找方法，再找方法上的类
		A annotation = handlerMethod.getMethodAnnotation(annotationType);
		if (null != annotation) {
			return annotation;
		}
		// 获取类上面的Annotation，可能包含组合注解，故采用spring的工具类
		Class<?> beanType = handlerMethod.getBeanType();
		return AnnotatedElementUtils.findMergedAnnotation(beanType, annotationType);
	}

	/**
	 * 做自己的白名单，允许base64的图片通过等
	 *
	 * @author michael
	 */
	public static class HtmlSafeList extends org.jsoup.safety.Safelist {

		public static final HtmlSafeList INSTANCE = new HtmlSafeList();

		public HtmlSafeList() {
			addTags("a", "b", "blockquote", "br", "caption", "cite", "code", "col", "colgroup", "dd", "div", "span",
					"embed", "object", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6", "i", "img", "li", "ol",
					"p", "pre", "q", "small", "strike", "strong", "sub", "sup", "table", "tbody", "td", "tfoot", "th",
					"thead", "tr", "u", "ul");

			addAttributes("a", "href", "title", "target");
			addAttributes("blockquote", "cite");
			addAttributes("col", "span");
			addAttributes("colgroup", "span");
			addAttributes("img", "align", "alt", "src", "title");
			addAttributes("ol", "start");
			addAttributes("q", "cite");
			addAttributes("table", "summary");
			addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width");
			addAttributes("th", "abbr", "axis", "colspan", "rowspan", "scope", "width");
			addAttributes("video", "src", "autoplay", "controls", "loop", "muted", "poster", "preload");
			addAttributes("object", "width", "height", "classid", "codebase");
			addAttributes("param", "name", "value");
			addAttributes("embed", "src", "quality", "width", "height", "allowFullScreen", "allowScriptAccess",
					"flashvars", "name", "type", "pluginspage");

			addAttributes(":all", "class", "style", "height", "width", "type", "id", "name");

			addProtocols("blockquote", "cite", "http", "https");
			addProtocols("cite", "cite", "http", "https");
			addProtocols("q", "cite", "http", "https");

			// 如果添加以下的协议，那么href 必须是http、 https 等开头，相对路径则被过滤掉了
			// addProtocols("a", "href", "ftp", "http", "https", "mailto", "tel");

			// 如果添加以下的协议，那么src必须是http 或者 https 开头，相对路径则被过滤掉了，
			// 所以必须注释掉，允许相对路径的图片资源
			// addProtocols("img", "src", "http", "https");
		}

		@Override
		protected boolean isSafeAttribute(String tagName, Element el, Attribute attr) {
			// 不允许 javascript 开头的 src 和 href
			if ("src".equalsIgnoreCase(attr.getKey()) || "href".equalsIgnoreCase(attr.getKey())) {
				String value = attr.getValue();
				if (StringUtils.hasText(value) && value.toLowerCase().startsWith("javascript")) {
					return false;
				}
			}
			// 允许 base64 的图片内容
			if ("img".equals(tagName) && "src".equals(attr.getKey()) && attr.getValue().startsWith("data:;base64")) {
				return true;
			}
			return super.isSafeAttribute(tagName, el, attr);
		}

	}

}
