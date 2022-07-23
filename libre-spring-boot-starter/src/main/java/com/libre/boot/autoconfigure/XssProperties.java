package com.libre.boot.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Xss配置类
 *
 * @author Libre
 */
@Getter
@Setter
@ConfigurationProperties(XssProperties.PREFIX)
public class XssProperties {
	public static final String PREFIX = "libre.xss";

	/**
	 * 开启xss
	 */
	private boolean enabled = Boolean.TRUE;
	/**
	 * 全局：对文件进行首尾 trim
	 */
	private boolean trimText = true;
	/**
	 * 模式：CLEAR 清理（默认），escape 转义
	 */
	private Mode mode = Mode.CLEAR;
	/**
	 * [CLEAR 专用] prettyPrint，默认关闭： 保留换行
	 */
	private boolean prettyPrint = false;
	/**
	 * [CLEAR 专用] 使用转义，默认关闭
	 */
	private boolean enableEscape = false;
	/**
	 * 拦截的路由，默认为空
	 */
	private List<String> pathPatterns = new ArrayList<>();
	/**
	 * 放行的路由，默认为空
	 */
	private List<String> pathExcludePatterns = new ArrayList<>();

	public enum Mode {
		/**
		 * 清理
		 */
		CLEAR,
		/**
		 * 转义
		 */
		ESCAPE
	}

}
