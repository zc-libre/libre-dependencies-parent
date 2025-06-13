package org.zclibre.redisson.cache;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.redisson.api.options.LocalCachedMapOptions;

import java.time.Duration;

/**
 * Redisson cache 配置
 *
 * @author Libre
 */
@Getter
@Setter
@ToString
public class RedissonCacheConfig {

	/**
	 * 缓存容量，值为0表示不限制本地缓存容量大小。
	 */
	private int maxSize = 0;

	/**
	 * 本地缓存里元素的有效时间。
	 */
	private Duration timeToLive = Duration.ZERO;

	/**
	 * 本地缓存里元素的最长闲置时间。
	 */
	private Duration maxIdleTime = Duration.ZERO;

	/**
	 * 淘汰策略： <br>
	 * LFU - 统计元素的使用频率，淘汰用得最少（最不常用）的。 LRU - 按元素使用时间排序比较，淘汰最早（最久远）的。 SOFT -
	 * 元素用Java的WeakReference来保存，缓存元素通过GC过程清除。 WEAK - 元素用Java的SoftReference来保存,
	 * 缓存元素通过GC过程清除。 NONE - 永不淘汰清除缓存元素。 </br>
	 */
	private LocalCachedMapOptions.EvictionPolicy evictionPolicy = LocalCachedMapOptions.EvictionPolicy.LRU;

	/**
	 * 同步策略: <br>
	 * INVALIDATE - 默认值。当本地缓存映射的某条元素发生变动时，同时驱逐所有相同本地缓存映射内的该元素 UPDATE -
	 * 当本地缓存映射的某条元素发生变动时，同时更新所有相同本地缓存映射内的该元素 NONE - 不做任何同步处理 </br>
	 */
	private LocalCachedMapOptions.SyncStrategy syncStrategy = LocalCachedMapOptions.SyncStrategy.INVALIDATE;

	/**
	 * 重连时的策略: <br>
	 * CLEAR - 如果断线一段时间以后则在重新建立连接以后清空本地缓存 LOAD -
	 * 在服务端保存一份10分钟的作废日志，如果10分钟内重新建立连接，则按照作废日志内的记录清空本地缓存的元素，如果断线时间超过了这个时间，则将清空本地缓存中所有的内容。
	 * NONE - 默认值。断线重连时不做处理。 </br>
	 */
	private LocalCachedMapOptions.ReconnectionStrategy reconnectionStrategy = LocalCachedMapOptions.ReconnectionStrategy.NONE;

}
