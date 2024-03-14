package com.libre.redisson.client;

import lombok.Getter;
import lombok.Setter;
import org.redisson.config.SslProvider;

import java.net.URL;

/**
 * 基础配置
 *
 * @author Libre
 */
@Getter
@Setter
public class BaseConfig {

	/**
	 * 连接空闲超时，单位：毫秒，默认值：10000
	 */
	private int idleConnectionTimeout = 10000;

	/**
	 * Ping timeout used in <code>Node.ping</code> and <code>Node.pingAll<code> operation.
	 * Value in milliseconds.
	 */
	private int pingTimeout = 1000;

	/**
	 * Timeout during connecting to any RedissonUtils server. Value in milliseconds.
	 */
	private int connectTimeout = 10000;

	/**
	 * RedissonUtils server response timeout. Starts to countdown when RedissonUtils
	 * command was succesfully sent. Value in milliseconds.
	 */
	private int timeout = 3000;

	private int retryAttempts = 3;

	private int retryInterval = 1500;

	/**
	 * Password for RedissonUtils authentication. Should be null if not needed
	 */
	private String password;

	/**
	 * Subscriptions per RedissonUtils connection limit
	 */
	private int subscriptionsPerConnection = 5;

	/**
	 * Name of client connection
	 */
	private String clientName;

	private boolean sslEnableEndpointIdentification = true;

	private SslProvider sslProvider = SslProvider.JDK;

	private URL sslTruststore;

	private String sslTruststorePassword;

	private URL sslKeystore;

	private String sslKeystorePassword;

	private int pingConnectionInterval;

	private boolean keepAlive;

	private boolean tcpNoDelay;

}
