package com.libre.boot.exception;

import com.google.common.base.Preconditions;
import com.libre.boot.toolkit.RequestUtils;
import com.libre.toolkit.result.R;
import com.libre.toolkit.result.ResultCode;
import com.libre.toolkit.core.Exceptions;
import com.libre.toolkit.core.StringPool;
import com.libre.toolkit.core.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * mica 未知异常转译和发送，方便监听，对未知异常统一处理。Order 排序优先级低
 *
 * @author Libre
 */
@Slf4j
@Order
@RestControllerAdvice
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
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
			ErrorUtil.publishEvent(e);
		}
		return result;
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public R<Object> handleError(Throwable e) {
		log.error("未知异常", e);
		// 发送：未知异常异常事件
		ErrorUtil.publishEvent(e);
		return R.fail(ResultCode.FAILURE);
	}


}
