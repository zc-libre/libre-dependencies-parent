package com.libre.boot.xss;

import com.libre.boot.autoconfigure.XssProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

/**
 * xss 处理拦截器
 *
 * @author Libre
 */
@RequiredArgsConstructor
public class XssCleanInterceptor implements AsyncHandlerInterceptor {
	private final XssProperties xssProperties;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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

		XssCleanIgnore xssCleanIgnore = getAnnotation(handlerMethod, XssCleanIgnore.class);
		if (xssCleanIgnore == null) {
			XssHolder.setEnable();
		}
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		XssHolder.remove();
	}

	@Override
	public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		XssHolder.remove();
	}

	private static <A extends Annotation> A getAnnotation(HandlerMethod handlerMethod, Class<A> annotationType) {
		// 先找方法，再找方法上的类
		A annotation = handlerMethod.getMethodAnnotation(annotationType);
		if (null != annotation) {
			return annotation;
		}
		// 获取类上面的Annotation，可能包含组合注解，故采用spring的工具类
		Class<?> beanType = handlerMethod.getBeanType();
		return AnnotatedElementUtils.findMergedAnnotation(beanType, annotationType);
	}
}
