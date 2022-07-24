package com.libre.monitor;

import lombok.Getter;
import lombok.Setter;

/**
 * 系统内存信息
 * <p>
 * 尊重知识产权，CV 请保留版权，爱组搭 http://aizuda.com 出品
 *
 * @author 青苗
 * @since 2022-03-02
 */
@Getter
@Setter
public class MemoryInfo {
    /**
     * 总计
     */
    private String total;

    /**
     * 已使用
     */
    private String used;

    /**
     * 未使用
     */
    private String free;

    /**
     * 使用率
     */
    private double usePercent;

}
