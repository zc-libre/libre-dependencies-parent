package com.libre.boot.toolkit;

import com.libre.toolkit.json.JsonUtil;
import com.libre.toolkit.core.StringPool;
import com.libre.toolkit.core.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author ZC
 * @date 2021/12/19 17:18
 */
@Slf4j
public class RequestUtils extends WebUtils {

	private static final String[] IP_HEADER_NAMES = new String[] { "x-forwarded-for", "Proxy-Client-IP",
			"WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP",
			"HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR", "X-Real-IP" };

	private static final Predicate<String> IS_BLANK_IP = (ip) -> StringUtil.isBlank(ip)
			|| StringPool.UNKNOWN.equalsIgnoreCase(ip);

	/**
	 * 获取 HttpServletRequest
	 * @return {@link HttpServletRequest}
	 */
	public static HttpServletRequest getRequest() {
		return Optional.ofNullable(RequestContextHolder.getRequestAttributes()).map(x -> (ServletRequestAttributes) x)
				.map(ServletRequestAttributes::getRequest).orElse(null);
	}

	/**
	 * 获取 HttpServletResponse
	 * @return {HttpServletResponse}
	 */
	@Nullable
	public static HttpServletResponse getResponse() {
		return Optional.ofNullable(RequestContextHolder.getRequestAttributes()).map(x -> (ServletRequestAttributes) x)
				.map(ServletRequestAttributes::getResponse).orElse(null);
	}

	/**
	 * 获取ip
	 * @return {String}
	 */
	@Nullable
	public static String getIp() {
		return Optional.ofNullable(RequestUtils.getRequest()).map(RequestUtils::getIp).orElse(null);
	}

	/**
	 * 获取ip
	 * @param request HttpServletRequest
	 * @return {String}
	 */
	@Nullable
	public static String getIp(@Nullable HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		String ip = null;
		for (String ipHeader : IP_HEADER_NAMES) {
			ip = request.getHeader(ipHeader);
			if (!IS_BLANK_IP.test(ip)) {
				break;
			}
		}
		if (IS_BLANK_IP.test(ip)) {
			ip = request.getRemoteAddr();
		}
		return StringUtil.isBlank(ip) ? null : getMultistageReverseProxyIp(ip);
	}

	/**
	 * 从多级反向代理中获得第一个非unknown IP地址
	 * @param ip 获得的IP地址
	 * @return 第一个非unknown IP地址
	 */
	private static String getMultistageReverseProxyIp(String ip) {
		// 多级反向代理检测
		String delimiter = StringPool.COMMA;
		if (ip != null && ip.indexOf(delimiter) > 0) {
			String[] ips = ip.trim().split(delimiter);
			for (String subIp : ips) {
				if (!IS_BLANK_IP.test(subIp)) {
					ip = subIp;
					break;
				}
			}
		}
		return ip;
	}

	/**
	 * 返回json
	 * @param response HttpServletResponse
	 * @param result 结果对象
	 */
	public static void renderJson(HttpServletResponse response, @Nullable Object result) {
		String jsonText = JsonUtil.toJson(result);
		if (jsonText != null) {
			renderText(response, jsonText, MediaType.APPLICATION_JSON_VALUE);
		}
	}

	/**
	 * 返回json
	 * @param response HttpServletResponse
	 * @param jsonText json 文本
	 */
	public static void renderJson(HttpServletResponse response, @Nullable String jsonText) {
		if (jsonText != null) {
			renderText(response, jsonText, MediaType.APPLICATION_JSON_VALUE);
		}
	}

	/**
	 * 返回json
	 * @param response HttpServletResponse
	 * @param text 文本
	 * @param contentType contentType
	 */
	public static void renderText(HttpServletResponse response, String text, String contentType) {
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(contentType);
		try (PrintWriter out = response.getWriter()) {
			out.append(text);
		}
		catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

}
