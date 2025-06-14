package org.zclibre.mybatis.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * @author ZC
 * @date 2022/1/14 22:02
 */
@AutoConfiguration
@EnableConfigurationProperties(LibreMyBatisProperties.class)
public class LibreMyBatisAutoConfiguration {

	/**
	 * 分页插件, 对于单一数据库类型来说,都建议配置该值,避免每次分页都去抓取数据库类型
	 */
	@Bean
	@ConditionalOnMissingBean
	public PaginationInnerInterceptor paginationInnerInterceptor(LibreMyBatisProperties properties) {
		PaginationInnerInterceptor interceptor = new PaginationInnerInterceptor(properties.getDbType());
		interceptor.setOverflow(properties.getOverflow());
		interceptor.setMaxLimit(properties.getMaxLimit());
		return interceptor;

	}

	@Bean
	@ConditionalOnMissingBean
	public MybatisPlusInterceptor mybatisPlusInterceptor(ObjectProvider<List<InnerInterceptor>> listObjectProvider) {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		listObjectProvider.ifAvailable(interceptorList -> interceptorList.forEach(interceptor::addInnerInterceptor));
		return interceptor;
	}

}
