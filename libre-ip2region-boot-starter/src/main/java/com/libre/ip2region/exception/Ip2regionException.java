package com.libre.ip2region.exception;

/**
 * @author: Libre
 * @Date: 2022/7/13 11:05 PM
 */
public class Ip2regionException extends RuntimeException {
	public Ip2regionException(String message) {
		super(message);
	}

	public Ip2regionException(String message, Throwable cause) {
		super(message, cause);
	}

	public Ip2regionException(Throwable cause) {
		super(cause);
	}
}
