package com.libre.boot.xss;

import lombok.Getter;

/**
 * xss 表单异常
 *
 * @author L.cm
 */
@Getter
public class FromXssException extends IllegalStateException implements XssException {

	private final String input;

	public FromXssException(String input, String message) {
		super(message);
		this.input = input;
	}

}
