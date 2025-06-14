package com.libre.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
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

	/**
	 * 添加分页插件
	 */
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor(LibreMyBatisProperties properties) {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
		// 如果配置多个插件, 切记分页最后添加
		// 如果有多数据源可以不配具体类型, 否则都建议配上具体的 DbType
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor(properties.getDbType()));
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
