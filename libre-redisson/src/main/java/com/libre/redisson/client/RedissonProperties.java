package com.libre.redisson.client;

import lombok.Data;
import org.redisson.client.codec.Codec;
import org.redisson.config.TransportMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Redisson 配置
 *
 * @author Libre
 */
@Data
@ConfigurationProperties(prefix = "libre.redisson")
public class RedissonProperties {

	/**
	 * 线程数，默认值: 当前处理核数量 * 2
	 */
	private Integer threads;

	/**
	 * Netty线程池数量，默认值: 当前处理核数量 * 2
	 */
	private Integer nettyThreads;

	/**
	 * 传输模式，默认值：NIO
	 */
	private TransportMode transportMode = TransportMode.NIO;

	/**
	 * 编码方式，默认为: FstCodec
	 */
	private Class<? extends Codec> codec;

	/**
	 * lua 脚本缓存，默认值：false
	 */
	private boolean useScriptCache = false;

	/**
	 * 单节点模式
	 */
	@NestedConfigurationProperty
	private SingleServerConfig single;

	/**
	 * 集群模式
	 */
	@NestedConfigurationProperty
	private ClusterServersConfig cluster;

	/**
	 * 主从模式
	 */
	@NestedConfigurationProperty
	private MasterSlaveServersConfig masterSlave;

	/**
	 * 哨兵模式
	 */
	@NestedConfigurationProperty
	private SentinelServersConfig sentinel;

	/**
	 * 云托管模式
	 */
	@NestedConfigurationProperty
	private ReplicatedServersConfig replicated;

}
