package org.zclibre.redisson.client;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 集群配置
 *
 * @author Libre
 */
@Getter
@Setter
public class ClusterServersConfig extends BaseMasterSlaveServersConfig {

	private Map<String, String> natMap = Collections.emptyMap();

	/**
	 * RedissonUtils cluster node urls list
	 */
	private List<String> nodeAddresses = new ArrayList<>();

	/**
	 * RedissonUtils cluster scan interval in milliseconds
	 */
	private int scanInterval = 5000;

}
