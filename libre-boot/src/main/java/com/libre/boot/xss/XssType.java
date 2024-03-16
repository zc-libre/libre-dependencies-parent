package com.libre.boot.xss;

import com.libre.toolkit.core.Exceptions;

/**
 * xss 数据处理类型
 */
public enum XssType {

	/**
	 * 表单
	 */
	FORM() {
		@Override
		public RuntimeException getXssException(String name, String input, String message) {
			return new FromXssException(input, message);
		}
	},

	/**
	 * body json
	 */
	JACKSON() {
		@Override
		public RuntimeException getXssException(String name, String input, String message) {
			return Exceptions.unchecked(new JacksonXssException(name, input, message));
		}
	};

	/**
	 * 获取 xss 异常
	 * @param name 属性名
	 * @param input input
	 * @param message message
	 * @return XssException
	 */
	public abstract RuntimeException getXssException(String name, String input, String message);

}
