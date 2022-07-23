package com.libre.mybatis.autoconfigure;

import com.libre.mybatis.dict.DictBind;
import com.libre.mybatis.dict.DictBindInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZC
 * @date 2021/11/21 14:34
 */
@Configuration(proxyBeanMethods = false)
public class DictBindAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public DictBindInterceptor dictBindInterceptor(@Autowired(required = false) DictBind dictBind) {
		return new DictBindInterceptor(dictBind);
	}
}
