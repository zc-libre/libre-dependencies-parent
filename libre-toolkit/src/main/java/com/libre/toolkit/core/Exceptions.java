package com.libre.toolkit.core;

import com.google.common.base.Throwables;
import com.libre.toolkit.exception.LibreException;
import lombok.experimental.UtilityClass;

import java.lang.reflect.InvocationTargetException;

/**
 * @author ZC
 * @date 2021/12/25 3:38
 */
@UtilityClass
public class Exceptions {

	public static String buildMessage(String message, Throwable cause) {
		if (cause == null) {
			return message;
		}
		StringBuilder sb = new StringBuilder(64);
		if (message != null) {
			sb.append(message).append("; ");
		}
		sb.append("nested exception is ").append(cause);
		return sb.toString();
	}

	public static Throwable getRootCause(Throwable throwable) {
		return Throwables.getRootCause(throwable);
	}

	public static String getStackTraceAsString(Throwable throwable) {
		return Throwables.getStackTraceAsString(throwable);
	}

	public static LibreException libreException(Throwable e) {
		return new LibreException(e);
	}

	/**
	 * 将CheckedException转换为UncheckedException.
	 * @param e Throwable
	 * @return {RuntimeException}
	 */
	public static RuntimeException unchecked(Throwable e) {
		if (e instanceof Error) {
			throw (Error) e;
		}
		else if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
				|| e instanceof NoSuchMethodException) {
			return new IllegalArgumentException(e);
		}
		else if (e instanceof InvocationTargetException) {
			return runtime(((InvocationTargetException) e).getTargetException());
		}
		else if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		else if (e instanceof InterruptedException) {
			Thread.currentThread().interrupt();
		}
		return runtime(e);
	}

	/**
	 * 不采用 RuntimeException 包装，直接抛出，使异常更加精准
	 * @param throwable Throwable
	 * @param <T> 泛型标记
	 * @return Throwable
	 * @throws T 泛型
	 */
	@SuppressWarnings("unchecked")
	private static <T extends Throwable> T runtime(Throwable throwable) throws T {
		throw (T) throwable;
	}

}
