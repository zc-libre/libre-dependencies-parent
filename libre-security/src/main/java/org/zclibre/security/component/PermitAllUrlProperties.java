package com.libre.security.component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author lengleng
 * @date 2020-03-11
 * <p>
 * 资源服务器对外直接暴露URL,如果设置contex-path 要特殊处理
 */
@Slf4j
@ConfigurationProperties(prefix = "security.oauth2.ignore")
public class PermitAllUrlProperties implements InitializingBean, ApplicationContextAware {

	private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");

	private static final String[] DEFAULT_IGNORE_URLS = new String[] { "/actuator/**", "/error", "/v3/api-docs" };

	@Getter
	@Setter
	private List<String> urls = new ArrayList<>();

	@Override
	public void afterPropertiesSet() {
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

	}

}
