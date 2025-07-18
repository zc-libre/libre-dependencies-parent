package org.zclibre.toolkit.exception;

/**
 * @author ZC
 * @date 2021/12/28 22:07
 */
public class LibreException extends RuntimeException {

	public LibreException(String message) {
		super(message);
	}

	public LibreException(String message, Object... args) {
		super(String.format(message, args));
	}

	public LibreException(String message, Throwable cause, Object... args) {
		super(String.format(message, args), cause);
	}

	public LibreException(String message, Throwable cause) {
		super(message, cause);
	}

	public LibreException(Throwable cause) {
		super(cause);
	}

}
