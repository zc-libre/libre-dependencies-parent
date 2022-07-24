package com.libre.monitor;

import lombok.Getter;
import lombok.Setter;

/**
 * 操作系统信息
 * <p>
 * 尊重知识产权，CV 请保留版权，爱组搭 <a href="http://aizuda.com">http://aizuda.com</a> 出品
 *
 * @author 青苗
 * @since 2022-03-02
 */
@Getter
@Setter
public class SysInfo {

    /**
     * 系统名称
     */
    private String name;

    /**
     * 系统 ip
     */
    private String ip;

    /**
     * 操作系统
     */
    private String osName;

    /**
     * 系统架构
     */
    private String osArch;

    /**
     * 项目路径
     */
    private String userDir;

}
