package com.libre.redisson.config;

import com.libre.redisson.client.*;
import jodd.util.StringUtil;
import org.redisson.Redisson;
import org.redisson.api.HostPortNatMapper;
import org.redisson.api.NatMapper;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;

/**
 * redisson 配置
 *
 * <p>
 * https://github.com/spring-projects/spring-boot/pull/19099
 * </p>
 *
 * @author Libre
 */
@AutoConfiguration
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonClientConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public RedissonClient redissonClient(Config config) {
		return Redisson.create(config);
	}

	@Bean
	@ConditionalOnMissingBean
	public Config config(RedissonProperties properties) {
		Config config = new Config();
		Integer threads = properties.getThreads();
		if (threads != null) {
			config.setThreads(threads);
		}
		Integer nettyThreads = properties.getNettyThreads();
		if (nettyThreads != null) {
			config.setNettyThreads(nettyThreads);
		}
		TransportMode transportMode1 = properties.getTransportMode();
		if (transportMode1 != null) {
			config.setTransportMode(transportMode1);
		}
		Class<? extends Codec> codec = properties.getCodec();
		if (codec != null) {
			config.setCodec(BeanUtils.instantiateClass(codec));
		}
		SingleServerConfig single = properties.getSingle();
		// 配置了 single 的地址
		if (single != null && StringUtil.isNotBlank(single.getAddress())) {
			return singleConfig(config, single);
		}
		ClusterServersConfig cluster = properties.getCluster();
		// 如果配置了 cluster 的地址
		if (cluster != null && !CollectionUtils.isEmpty(cluster.getNodeAddresses())) {
			return clusterConfig(config, cluster);
		}
		SentinelServersConfig sentinel = properties.getSentinel();
		// 如果配置了 sentinel 的地址
		if (sentinel != null && !CollectionUtils.isEmpty(sentinel.getSentinelAddresses())) {
			return sentinelConfig(config, sentinel);
		}
		MasterSlaveServersConfig masterSlave = properties.getMasterSlave();
		// 如果配置了 masterSlave 的地址
		if (masterSlave != null && StringUtil.isNotBlank(masterSlave.getMasterAddress())) {
			return masterSlaveConfig(config, masterSlave);
		}
		ReplicatedServersConfig replicated = properties.getReplicated();
		// 如果配置了 replicated 的地址
		if (replicated != null && !CollectionUtils.isEmpty(replicated.getNodeAddresses())) {
			return replicatedConfig(config, replicated);
		}
		throw new IllegalArgumentException("Redisson config error, please add redis server address.");
	}

	private static Config sentinelConfig(Config config, SentinelServersConfig sentinel) {
		config.useSentinelServers()
			// 私有配置
			.addSentinelAddress(repairAddressIfNeed(sentinel.getSentinelAddresses()))
			.setMasterName(sentinel.getMasterName())
			.setDatabase(sentinel.getDatabase())
			.setScanInterval(sentinel.getScanInterval())
			.setNatMapper(buildNatMapper(sentinel.getNatMap()))
			// 父类配置
			.setLoadBalancer(sentinel.getLoadBalancer().getLb())
			.setMasterConnectionPoolSize(sentinel.getMasterConnectionPoolSize())
			.setSlaveConnectionPoolSize(sentinel.getSlaveConnectionPoolSize())
			.setSubscriptionConnectionPoolSize(sentinel.getSubscriptionConnectionPoolSize())
			.setMasterConnectionMinimumIdleSize(sentinel.getMasterConnectionMinimumIdleSize())
			.setSlaveConnectionMinimumIdleSize(sentinel.getSlaveConnectionMinimumIdleSize())
			.setSubscriptionConnectionMinimumIdleSize(sentinel.getSubscriptionConnectionMinimumIdleSize())
			.setReadMode(sentinel.getReadMode())
			.setSubscriptionMode(sentinel.getSubscriptionMode())
			.setDnsMonitoringInterval(sentinel.getDnsMonitoringInterval())
			.setFailedSlaveCheckInterval(sentinel.getFailedSlaveCheckInterval())
			.setFailedSlaveReconnectionInterval(sentinel.getFailedSlaveReconnectionInterval())
			// 公有配置
			.setPassword(sentinel.getPassword())
			.setSubscriptionsPerConnection(sentinel.getSubscriptionsPerConnection())
			.setRetryAttempts(sentinel.getRetryAttempts())
			.setRetryInterval(sentinel.getRetryInterval())
			.setTimeout(sentinel.getTimeout())
			.setClientName(sentinel.getClientName())
			.setConnectTimeout(sentinel.getConnectTimeout())
			.setIdleConnectionTimeout(sentinel.getIdleConnectionTimeout())
			.setSslEnableEndpointIdentification(sentinel.isSslEnableEndpointIdentification())
			.setSslProvider(sentinel.getSslProvider())
			.setSslTruststore(sentinel.getSslTruststore())
			.setSslTruststorePassword(sentinel.getSslTruststorePassword())
			.setSslKeystore(sentinel.getSslKeystore())
			.setSslKeystorePassword(sentinel.getSslKeystorePassword())
			.setPingConnectionInterval(sentinel.getPingConnectionInterval())
			.setKeepAlive(sentinel.isKeepAlive())
			.setTcpNoDelay(sentinel.isTcpNoDelay());
		return config;
	}

	private static Config clusterConfig(Config config, ClusterServersConfig cluster) {
		config.useClusterServers()
			// 私有配置
			.addNodeAddress(repairAddressIfNeed(cluster.getNodeAddresses()))
			.setScanInterval(cluster.getScanInterval())
			.setNatMapper(buildNatMapper(cluster.getNatMap()))
			// 父类配置
			.setLoadBalancer(cluster.getLoadBalancer().getLb())
			.setMasterConnectionPoolSize(cluster.getMasterConnectionPoolSize())
			.setSlaveConnectionPoolSize(cluster.getSlaveConnectionPoolSize())
			.setSubscriptionConnectionPoolSize(cluster.getSubscriptionConnectionPoolSize())
			.setMasterConnectionMinimumIdleSize(cluster.getMasterConnectionMinimumIdleSize())
			.setSlaveConnectionMinimumIdleSize(cluster.getSlaveConnectionMinimumIdleSize())
			.setSubscriptionConnectionMinimumIdleSize(cluster.getSubscriptionConnectionMinimumIdleSize())
			.setReadMode(cluster.getReadMode())
			.setSubscriptionMode(cluster.getSubscriptionMode())
			.setDnsMonitoringInterval(cluster.getDnsMonitoringInterval())
			.setFailedSlaveCheckInterval(cluster.getFailedSlaveCheckInterval())
			.setFailedSlaveReconnectionInterval(cluster.getFailedSlaveReconnectionInterval())
			// 公有配置
			.setPassword(cluster.getPassword())
			.setSubscriptionsPerConnection(cluster.getSubscriptionsPerConnection())
			.setRetryAttempts(cluster.getRetryAttempts())
			.setRetryInterval(cluster.getRetryInterval())
			.setTimeout(cluster.getTimeout())
			.setClientName(cluster.getClientName())
			.setConnectTimeout(cluster.getConnectTimeout())
			.setIdleConnectionTimeout(cluster.getIdleConnectionTimeout())
			.setSslEnableEndpointIdentification(cluster.isSslEnableEndpointIdentification())
			.setSslProvider(cluster.getSslProvider())
			.setSslTruststore(cluster.getSslTruststore())
			.setSslTruststorePassword(cluster.getSslTruststorePassword())
			.setSslKeystore(cluster.getSslKeystore())
			.setSslKeystorePassword(cluster.getSslKeystorePassword())
			.setPingConnectionInterval(cluster.getPingConnectionInterval())
			.setKeepAlive(cluster.isKeepAlive())
			.setTcpNoDelay(cluster.isTcpNoDelay());
		return config;
	}

	private static Config singleConfig(Config config, SingleServerConfig single) {
		config.useSingleServer()
			.setAddress(repairAddressIfNeed(single.getAddress()))
			// 通用配置
			.setPassword(single.getPassword())
			.setSubscriptionsPerConnection(single.getSubscriptionsPerConnection())
			.setRetryAttempts(single.getRetryAttempts())
			.setRetryInterval(single.getRetryInterval())
			.setTimeout(single.getTimeout())
			.setClientName(single.getClientName())
			.setConnectTimeout(single.getConnectTimeout())
			.setIdleConnectionTimeout(single.getIdleConnectionTimeout())
			.setSslEnableEndpointIdentification(single.isSslEnableEndpointIdentification())
			.setSslProvider(single.getSslProvider())
			.setSslTruststore(single.getSslTruststore())
			.setSslTruststorePassword(single.getSslTruststorePassword())
			.setSslKeystore(single.getSslKeystore())
			.setSslKeystorePassword(single.getSslKeystorePassword())
			.setPingConnectionInterval(single.getPingConnectionInterval())
			.setKeepAlive(single.isKeepAlive())
			.setTcpNoDelay(single.isTcpNoDelay())
			// 私有配置
			.setConnectionPoolSize(single.getConnectionPoolSize())
			.setSubscriptionConnectionPoolSize(single.getSubscriptionConnectionPoolSize())
			.setDnsMonitoringInterval(single.getDnsMonitoringInterval())
			.setSubscriptionConnectionMinimumIdleSize(single.getSubscriptionConnectionMinimumIdleSize())
			.setConnectionMinimumIdleSize(single.getConnectionMinimumIdleSize())
			.setDatabase(single.getDatabase());
		return config;
	}


	private static Config masterSlaveConfig(Config config, MasterSlaveServersConfig masterSlave) {
		config.useMasterSlaveServers()
			// 私有配置
			.setMasterAddress(repairAddressIfNeed(masterSlave.getMasterAddress()))
			.addSlaveAddress(repairAddressIfNeed(masterSlave.getSlaveAddresses()))
			.setDatabase(masterSlave.getDatabase())
			// 父类配置
			.setLoadBalancer(masterSlave.getLoadBalancer().getLb())
			.setMasterConnectionPoolSize(masterSlave.getMasterConnectionPoolSize())
			.setSlaveConnectionPoolSize(masterSlave.getSlaveConnectionPoolSize())
			.setSubscriptionConnectionPoolSize(masterSlave.getSubscriptionConnectionPoolSize())
			.setMasterConnectionMinimumIdleSize(masterSlave.getMasterConnectionMinimumIdleSize())
			.setSlaveConnectionMinimumIdleSize(masterSlave.getSlaveConnectionMinimumIdleSize())
			.setSubscriptionConnectionMinimumIdleSize(masterSlave.getSubscriptionConnectionMinimumIdleSize())
			.setReadMode(masterSlave.getReadMode())
			.setSubscriptionMode(masterSlave.getSubscriptionMode())
			.setDnsMonitoringInterval(masterSlave.getDnsMonitoringInterval())
			.setFailedSlaveCheckInterval(masterSlave.getFailedSlaveCheckInterval())
			.setFailedSlaveReconnectionInterval(masterSlave.getFailedSlaveReconnectionInterval())
			// 公有配置
			.setPassword(masterSlave.getPassword())
			.setSubscriptionsPerConnection(masterSlave.getSubscriptionsPerConnection())
			.setRetryAttempts(masterSlave.getRetryAttempts())
			.setRetryInterval(masterSlave.getRetryInterval())
			.setTimeout(masterSlave.getTimeout())
			.setClientName(masterSlave.getClientName())
			.setConnectTimeout(masterSlave.getConnectTimeout())
			.setIdleConnectionTimeout(masterSlave.getIdleConnectionTimeout())
			.setSslEnableEndpointIdentification(masterSlave.isSslEnableEndpointIdentification())
			.setSslProvider(masterSlave.getSslProvider())
			.setSslTruststore(masterSlave.getSslTruststore())
			.setSslTruststorePassword(masterSlave.getSslTruststorePassword())
			.setSslKeystore(masterSlave.getSslKeystore())
			.setSslKeystorePassword(masterSlave.getSslKeystorePassword())
			.setPingConnectionInterval(masterSlave.getPingConnectionInterval())
			.setKeepAlive(masterSlave.isKeepAlive())
			.setTcpNoDelay(masterSlave.isTcpNoDelay());
		return config;
	}

	private static Config replicatedConfig(Config config, ReplicatedServersConfig replicated) {
		config.useReplicatedServers()
			// 私有配置
			.addNodeAddress(repairAddressIfNeed(replicated.getNodeAddresses()))
			.setScanInterval(replicated.getScanInterval())
			.setDatabase(replicated.getDatabase())
			// 父类配置
			.setLoadBalancer(replicated.getLoadBalancer().getLb())
			.setMasterConnectionPoolSize(replicated.getMasterConnectionPoolSize())
			.setSlaveConnectionPoolSize(replicated.getSlaveConnectionPoolSize())
			.setSubscriptionConnectionPoolSize(replicated.getSubscriptionConnectionPoolSize())
			.setMasterConnectionMinimumIdleSize(replicated.getMasterConnectionMinimumIdleSize())
			.setSlaveConnectionMinimumIdleSize(replicated.getSlaveConnectionMinimumIdleSize())
			.setSubscriptionConnectionMinimumIdleSize(replicated.getSubscriptionConnectionMinimumIdleSize())
			.setReadMode(replicated.getReadMode())
			.setSubscriptionMode(replicated.getSubscriptionMode())
			.setDnsMonitoringInterval(replicated.getDnsMonitoringInterval())
			.setFailedSlaveCheckInterval(replicated.getFailedSlaveCheckInterval())
			.setFailedSlaveReconnectionInterval(replicated.getFailedSlaveReconnectionInterval())
			// 公有配置
			.setPassword(replicated.getPassword())
			.setSubscriptionsPerConnection(replicated.getSubscriptionsPerConnection())
			.setRetryAttempts(replicated.getRetryAttempts())
			.setRetryInterval(replicated.getRetryInterval())
			.setTimeout(replicated.getTimeout())
			.setClientName(replicated.getClientName())
			.setConnectTimeout(replicated.getConnectTimeout())
			.setIdleConnectionTimeout(replicated.getIdleConnectionTimeout())
			.setSslEnableEndpointIdentification(replicated.isSslEnableEndpointIdentification())
			.setSslProvider(replicated.getSslProvider())
			.setSslTruststore(replicated.getSslTruststore())
			.setSslTruststorePassword(replicated.getSslTruststorePassword())
			.setSslKeystore(replicated.getSslKeystore())
			.setSslKeystorePassword(replicated.getSslKeystorePassword())
			.setPingConnectionInterval(replicated.getPingConnectionInterval())
			.setKeepAlive(replicated.isKeepAlive())
			.setTcpNoDelay(replicated.isTcpNoDelay());
		return config;
	}

	/**
	 * 修复 redisson 地址
	 *
	 * @param address 地址
	 * @return 修复后的地址
	 */
	private static String repairAddressIfNeed(String address) {
		if (address.startsWith("redis")) {
			return address;
		}
		return "redis://" + address;
	}

	/**
	 * 修复 redisson 地址
	 *
	 * @param addressList 地址列表
	 * @return 修复后的地址
	 */
	private static String[] repairAddressIfNeed(Collection<String> addressList) {
		return addressList.stream()
			.map(RedissonClientConfiguration::repairAddressIfNeed)
			.distinct()
			.toArray(String[]::new);
	}

	/**
	 * 构建 NatMapper
	 *
	 * @param natMap natMap
	 * @return HostPortNatMapper
	 */
	private static NatMapper buildNatMapper(Map<String, String> natMap) {
		HostPortNatMapper mapper = new HostPortNatMapper();
		mapper.setHostsPortMap(natMap);
		return mapper;
	}

}
