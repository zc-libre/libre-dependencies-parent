package org.zclibre.boot.exception;

import org.zclibre.toolkit.core.Exceptions;
import org.zclibre.toolkit.core.ObjectUtil;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

/**
 * 异常工具类
 *
 * @author Libre
 */
@UtilityClass
public class ErrorUtil {

	/**
	 * 初始化异常信息
	 * @param error 异常
	 * @param event 异常事件封装
	 */
	public static void initErrorInfo(Throwable error, LibreErrorEvent event) {
		// 堆栈信息
		event.setStackTrace(Exceptions.getStackTraceAsString(error));
		event.setExceptionName(error.getClass().getName());
		event.setMessage(error.getMessage());
		event.setCreateTime(LocalDateTime.now());
		StackTraceElement[] elements = error.getStackTrace();
		if (ObjectUtil.isNotEmpty(elements)) {
			// 报错的类信息
			StackTraceElement element = elements[0];
			event.setClassName(element.getClassName());
			event.setFileName(element.getFileName());
			event.setMethodName(element.getMethodName());
			event.setLineNumber(element.getLineNumber());
		}
	}

}
