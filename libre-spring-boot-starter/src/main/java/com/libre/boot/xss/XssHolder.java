package com.libre.boot.xss;

import org.springframework.util.ObjectUtils;

import java.util.Objects;

/**
 * 利用 ThreadLocal 缓存线程间的数据
 *
 * @author L.cm
 */
public class XssHolder {
	private static final ThreadLocal<XssCleanIgnore> TL = new ThreadLocal<>();

	/**
	 * 是否开启
	 *
	 * @return boolean
	 */
	public static boolean isEnabled() {
		return Objects.isNull(TL.get());
	}

	/**
	 * 判断是否被忽略
	 *
	 * @return XssCleanIgnore
	 */
	static boolean isIgnore(String name) {
		XssCleanIgnore cleanIgnore = TL.get();
		if (cleanIgnore == null) {
			return false;
		}
		String[] ignoreArray = cleanIgnore.value();
		// 指定忽略的属性
		return ObjectUtils.containsElement(ignoreArray, name);
	}

	/**
	 * 标记为开启
	 */
	static void setIgnore(XssCleanIgnore xssCleanIgnore) {
		TL.set(xssCleanIgnore);
	}

	/**
	 * 关闭 xss 清理
	 */
	public static void remove() {
		TL.remove();
	}

}
