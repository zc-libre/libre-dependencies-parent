package com.libre.boot.autoconfigure;

import com.libre.boot.exception.ErrorType;
import com.libre.boot.exception.ErrorUtil;
import com.libre.boot.exception.LibreErrorEvent;
import com.libre.boot.executor.RunnableWrapper;
import com.libre.toolkit.constant.LibreConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.task.TaskExecutorCustomizer;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.ErrorHandler;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhao.cheng
 * @date 2021/3/3 10:45
 */
@Slf4j
@EnableAsync
@EnableScheduling
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class LibreExecutorAutoConfiguration extends AsyncConfigurerSupport {

	private final Environment environment;
	private final ApplicationEventPublisher eventPublisher;

	@Bean
	public TaskExecutorCustomizer taskExecutorCustomizer() {
		return taskExecutor -> {
			taskExecutor.setThreadNamePrefix("async-task-");
			taskExecutor.setTaskDecorator(RunnableWrapper::new);
			taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		};
	}

	@Bean
	public TaskSchedulerCustomizer taskSchedulerCustomizer() {
		return taskExecutor -> {
			taskExecutor.setThreadNamePrefix("scheduler-task-");
			taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
			taskExecutor.setErrorHandler(new LibreErrorHandler(environment, eventPublisher));
		};
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new LibreAsyncUncaughtExceptionHandler(environment, eventPublisher);
	}

	@RequiredArgsConstructor
	private static class LibreAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
		private final Environment environment;
		private final ApplicationEventPublisher eventPublisher;

		@Override
		public void handleUncaughtException(Throwable error, Method method, Object... params) {
			log.error("Unexpected exception occurred invoking async method: {}", method, error);
			LibreErrorEvent event = new LibreErrorEvent();
			event.setErrorType(ErrorType.ASYNC);
			// 服务信息、环境、异常类型
			event.setAppName(environment.getRequiredProperty(LibreConstants.SPRING_APP_NAME_KEY));
			event.setEnv(environment.getProperty(LibreConstants.ACTIVE_PROFILES_PROPERTY, ""));
			// 堆栈信息
			ErrorUtil.initErrorInfo(error, event);
			// 发布事件
			eventPublisher.publishEvent(event);
		}
	}

	@RequiredArgsConstructor
	private static class LibreErrorHandler implements ErrorHandler {
        private final Environment environment;
		private final ApplicationEventPublisher eventPublisher;

		@Override
		public void handleError(Throwable error) {
			log.error("Unexpected scheduler exception", error);
			LibreErrorEvent event = new LibreErrorEvent();
			// 服务信息、环境、异常类型
			event.setErrorType(ErrorType.SCHEDULER);
			event.setAppName(environment.getRequiredProperty(LibreConstants.SPRING_APP_NAME_KEY));
			event.setEnv(environment.getRequiredProperty(LibreConstants.ACTIVE_PROFILES_PROPERTY));
			// 堆栈信息
			ErrorUtil.initErrorInfo(error, event);
			// 发布事件
			eventPublisher.publishEvent(event);
		}
	}
}
