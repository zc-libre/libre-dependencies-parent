package com.libre.monitor;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import oshi.SystemInfo;

/**
 * 系统监控限制配置
 * <p>
 * 尊重知识产权，CV 请保留版权，爱组搭 <a href="http://aizuda.com">http://aizuda.com</a> 出品
 *
 * @author 青苗
 * @since 2022-03-02
 */
@AutoConfiguration
public class MonitorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OshiMonitor oshiMonitor() {
        return new OshiMonitor(new SystemInfo());
    }
}
