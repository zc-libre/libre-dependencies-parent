package com.libre.monitor;

import lombok.Getter;
import lombok.Setter;

/**
 * CPU 信息
 * <p>
 * 尊重知识产权，CV 请保留版权，爱组搭 <a href="http://aizuda.com">http://aizuda.com</a> 出品
 *
 * @author 青苗
 * @since 2022-03-02
 */
@Getter
@Setter
public class CpuInfo {

	/**
	 * 物理处理器数量
	 */
	private int physicalProcessorCount;

	/**
	 * 逻辑处理器数量
	 */
	private int logicalProcessorCount;

	/**
	 * 系统使用率
	 */
	private double systemPercent;

	/**
	 * 用户使用率
	 */
	private double userPercent;

	/**
	 * 当前等待率
	 */
	private double waitPercent;

	/**
	 * 当前使用率
	 */
	private double usePercent;

}
