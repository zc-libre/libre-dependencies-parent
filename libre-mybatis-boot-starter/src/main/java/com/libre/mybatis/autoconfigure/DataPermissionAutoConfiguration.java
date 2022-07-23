package com.libre.mybatis.autoconfigure;

import com.libre.mybatis.permission.DataScopeInterceptor;
import com.libre.mybatis.permission.IDataScopeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhao.cheng
 * @date 2021/4/20 13:55
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "libre.data-permission", name = "enabled", havingValue = "true")
@AutoConfigureAfter(LibreMyBatisAutoConfiguration.class)
public class DataPermissionAutoConfiguration {

	private final IDataScopeProvider dataScopeProvider;

	public DataPermissionAutoConfiguration(@Autowired(required = false)IDataScopeProvider dataScopeProvider) {
		this.dataScopeProvider = dataScopeProvider;
	}

	@Bean
    public DataScopeInterceptor dataScopeInterceptor() {
    	return new DataScopeInterceptor(dataScopeProvider);
	}
}
