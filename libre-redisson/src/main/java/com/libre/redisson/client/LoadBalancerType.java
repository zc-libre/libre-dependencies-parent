package com.libre.redisson.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.redisson.connection.balancer.LoadBalancer;
import org.redisson.connection.balancer.RandomLoadBalancer;
import org.redisson.connection.balancer.RoundRobinLoadBalancer;

/**
 * 负载均衡算法
 *
 * @author Libre
 */
@Getter
@RequiredArgsConstructor
public enum LoadBalancerType {

	/**
	 * 轮询
	 */
	ROUND_ROBIN(new RoundRobinLoadBalancer()),

	/**
	 * 随机
	 */
	RANDOM(new RandomLoadBalancer());

	public final LoadBalancer lb;

}
