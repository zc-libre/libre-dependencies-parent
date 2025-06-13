package org.zclibre.boot.exception;

import com.google.common.base.Preconditions;
import org.zclibre.boot.toolkit.RequestUtils;
import org.zclibre.toolkit.result.R;
import org.zclibre.toolkit.result.ResultCode;
import org.zclibre.toolkit.core.Exceptions;
import org.zclibre.toolkit.core.StringPool;
import org.zclibre.toolkit.core.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;
import jakarta.servlet.Servlet;

import java.time.LocalDateTime;

/**
 * 未知异常转译和发送，方便监听，对未知异常统一处理。Order 排序优先级低
 *
 * @author Libre
 */
@Slf4j
@Order
@RestControllerAdvice
@AutoConfiguration
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RequiredArgsConstructor
public class GlobalExceptionTranslator {

	private final ApplicationEventPublisher publisher;

	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public R<Object> handleError(BusinessException e) {
		log.error("业务异常", e);
		R<Object> result = e.getResult();
		if (result == null) {
			// 发送：未知业务异常事件
			result = R.fail(ResultCode.FAILURE, e.getMessage());
			publishEvent(e);
		}
		return result;
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public R<Object> handleError(Throwable e) {
		log.error("未知异常", e);
		// 发送：未知异常异常事件
		publishEvent(e);
		return R.fail(ResultCode.FAILURE);
	}

	private void publishEvent(Throwable error) {
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
		publisher.publishEvent(event);
	}

	/**
	 * 初始化异常信息
	 * @param error 异常
	 * @param event 异常事件封装
	 */
	private static void initErrorInfo(Throwable error, LibreErrorEvent event) {
		// 堆栈信息
		event.setStackTrace(Exceptions.getStackTraceAsString(error));
		event.setExceptionName(error.getClass().getName());
		event.setMessage(error.getMessage());
		event.setCreateTime(LocalDateTime.now());
		StackTraceElement[] elements = error.getStackTrace();
		if (!ObjectUtils.isEmpty(elements)) {
			// 报错的类信息
			StackTraceElement element = elements[0];
			event.setClassName(element.getClassName());
			event.setFileName(element.getFileName());
			event.setMethodName(element.getMethodName());
			event.setLineNumber(element.getLineNumber());
		}
	}

}
