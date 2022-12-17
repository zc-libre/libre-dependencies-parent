package com.libre.boot.exception;

import com.google.common.base.Preconditions;
import com.libre.boot.autoconfigure.SpringContext;
import com.libre.boot.toolkit.RequestUtils;
import com.libre.toolkit.core.Exceptions;
import com.libre.toolkit.core.ObjectUtil;
import com.libre.toolkit.core.StringPool;
import com.libre.toolkit.core.StringUtil;
import lombok.experimental.UtilityClass;

import javax.servlet.http.HttpServletRequest;
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
	 *
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

	public static void publishEvent(Throwable error) {
		LibreErrorEvent event = new LibreErrorEvent();
		// 服务异常类型
		event.setErrorType(ErrorType.REQUEST);
		// 异步获取不到的一些信息
		HttpServletRequest request = RequestUtils.getRequest();
		// 请求方法名
		Preconditions.checkNotNull(request, "request must not be null");
		event.setRequestMethod(request.getMethod());
		// 拼接地址
		String requestUrl = request.getRequestURI();
		String queryString = request.getQueryString();
		if (StringUtil.isNotBlank(queryString)) {
			requestUrl = requestUrl + StringPool.QUESTION_MARK + queryString;
		}
		// 请求ip
		event.setRequestIp(RequestUtils.getIp(request));
		event.setRequestUrl(requestUrl);
		// 堆栈信息
		initErrorInfo(error, event);
		// 发布事件，其他参数可监听时异步获取
		SpringContext.publishEvent(event);
	}
}
