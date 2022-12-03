package com.libre.boot.xss;

import com.libre.boot.autoconfigure.XssProperties;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * xss 处理拦截器
 *
 * @author L.cm
 */
@RequiredArgsConstructor
public class XssCleanInterceptor implements AsyncHandlerInterceptor {

	private final XssProperties xssProperties;

	@Override
	public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
			@NotNull Object handler) throws Exception {
		// 1. 非控制器请求直接跳出
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}
		// 2. 没有开启
		if (!xssProperties.isEnabled()) {
			return true;
		}
		// 3. 处理 XssIgnore 注解
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		XssCleanIgnore xssCleanIgnore = XssUtil.getAnnotation(handlerMethod, XssCleanIgnore.class);
		if (xssCleanIgnore == null) {
			XssHolder.setEnable();
		}
		return true;
	}

	@Override
	public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
			@NotNull Object handler, Exception ex) throws Exception {
		XssHolder.remove();
	}

	@Override
	public void afterConcurrentHandlingStarted(@NotNull HttpServletRequest request,
			@NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
		XssHolder.remove();
	}

}
