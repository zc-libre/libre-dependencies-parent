package com.libre.monitor;

import lombok.Getter;
import lombok.Setter;

/**
 * JVM 信息
 * <p>
 * 尊重知识产权，CV 请保留版权，爱组搭 http://aizuda.com 出品
 *
 * @author 青苗
 * @since 2022-03-02
 */
@Getter
@Setter
public class JvmInfo {

	/**
	 * jdk版本
	 */
	String jdkVersion;

	/**
	 * jdk Home
	 */
	String jdkHome;

	/**
	 * jak name
	 */
	private String jdkName;

	/**
	 * 总内存
	 */
	String jvmTotalMemory;

	/**
	 * Java虚拟机将尝试使用的最大内存量
	 */
	String maxMemory;

	/**
	 * 空闲内存
	 */
	String freeMemory;

	/**
	 * 已使用内存
	 */
	String usedMemory;

	/**
	 * 内存使用率
	 */
	private double usePercent;

	/**
	 * 返回Java虚拟机的启动时间（毫秒）。此方法返回Java虚拟机启动的大致时间。
	 */
	private long startTime;

	/**
	 * 返回Java虚拟机的正常运行时间（毫秒）
	 */
	private long uptime;

}
