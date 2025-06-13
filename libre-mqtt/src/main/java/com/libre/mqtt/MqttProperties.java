package com.libre.mqtt;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.integration.mqtt.core.ClientManager;
import org.springframework.validation.annotation.Validated;

import javax.net.ssl.HostnameVerifier;
import java.time.Duration;
import java.util.Properties;

/**
 * Configuration properties for MQTT integration.
 *
 * <p>This class provides comprehensive configuration options for MQTT client connections,
 * including support for both MQTT v3.1.1 and MQTT v5 protocols. It allows configuration
 * of connection parameters, SSL settings, producer and consumer behaviors, and thread pool
 * settings for message processing.
 *
 * <p>Example configuration:
 * <pre>
 * libre:
 *   mqtt:
 *     enabled: true
 *     protocol-version: V5
 *     urls:
 *       - tcp://localhost:1883
 *       - ssl://localhost:8883
 *     username: admin
 *     password: secret
 *     producer:
 *       default-topic: events
 *       async: true
 *     consumer:
 *       qos: 1
 *       auto-startup: true
 * </pre>
 *
 * @author libre-mqtt
 * @since 1.0.0
 * @see org.springframework.integration.mqtt.core.MqttPahoClientFactory
 * @see org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter
 * @see org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler
 */
@Data
@ConfigurationProperties("libre.mqtt")
public class MqttProperties {

	/**
	 * The name of the MQTT input channel for receiving messages.
	 */
	public static final String MQTT_INPUT_CHANNEL_NAME = "mqttInputChannel";

	/**
	 * The name of the MQTT outbound channel for sending messages.
	 */
	public static final String MQTT_OUT_BOUND_CHANNEL_NAME = "mqttOutboundChannel";

	/**
	 * The name of the MQTT consumer executor bean.
	 */
	public static final String MQTT_CONSUMER_EXECUTOR = "mqttConsumerExecutor";

	/**
	 * Whether MQTT integration is enabled.
	 */
	private Boolean enabled = true;

	/**
	 * MQTT protocol version to use.
	 */
	private ProtocolVersion protocolVersion = ProtocolVersion.V3_1_1;

	/**
	 * MQTT broker URLs. Multiple URLs can be provided for failover support.
	 * Supported schemes: tcp://, ssl://, ws://, wss://
	 */
	private String[] urls = { "tcp://127.0.0.1:1883" };

	/**
	 * Username for MQTT broker authentication.
	 */
	private String username = "admin";

	/**
	 * Password for MQTT broker authentication.
	 */
	private String password = "public";

	/**
	 * Keep alive interval in seconds. Must be between 1 and 65535.
	 */
	private Integer keepAliveInterval = 60;

	/**
	 * Maximum number of inflight messages. Must be between 1 and 65535.
	 */
	private Integer maxInflight = 10;

	/**
	 * Will message destination topic.
	 */
	private String willDestination;

	/**
	 * Will message configuration.
	 */
	private MqttMessage willMessage = new MqttMessage();

	/**
	 * SSL client properties for secure connections.
	 */
	private Properties sslClientProps;

	/**
	 * Whether HTTPS hostname verification is enabled for SSL connections.
	 */
	private Boolean httpsHostnameVerificationEnabled = false;

	/**
	 * Custom SSL hostname verifier for secure connections.
	 */
	private HostnameVerifier sslHostnameVerifier;

	/**
	 * Whether to start with a clean session. If false, the broker will maintain
	 * subscription and message state across client reconnections.
	 */
	private Boolean cleanSession = true;

	/**
	 * Connection timeout in seconds. Must be between 1 and 3600.
	 */
	private Integer connectionTimeout = 30;

	/**
	 * Whether automatic reconnection is enabled.
	 */
	private Boolean automaticReconnect = true;

	/**
	 * Maximum reconnection delay in milliseconds. Must be between 1000 and 300000.
	 */
	private Integer maxReconnectDelay = 128000;

	/**
	 * Custom WebSocket headers for WebSocket connections.
	 */
	private Properties customWebSocketHeaders;

	/**
	 * How long to wait in seconds when terminating the executor service.
	 * Must be between 1 and 60.
	 */
	private Integer executorServiceTimeout = 1;

	/**
	 * Connection retry configuration.
	 */
	private Retry retry = new Retry();

	/**
	 * Producer (outbound) configuration.
	 */
	private Producer producer = new Producer();

	/**
	 * Consumer (inbound) configuration.
	 */
	private Consumer consumer = new Consumer();

	/**
	 * MQTT protocol version enumeration.
	 */
	public enum ProtocolVersion {
		/**
		 * MQTT version 3.1.1
		 */
		V3_1_1("3.1.1"),
		/**
		 * MQTT version 5.0
		 */
		V5("5.0");

		private final String version;

		ProtocolVersion(String version) {
			this.version = version;
		}

		public String getVersion() {
			return version;
		}
	}

	/**
	 * Retry configuration for connection failures.
	 */
	@Getter
	@Setter
	public static class Retry {

		/**
		 * Whether retry is enabled.
		 */
		private Boolean enabled = true;

		/**
		 * Maximum number of retry attempts.
		 */
		private Integer maxAttempts = 3;

		/**
		 * Initial retry delay.
		 */
		private Duration initialDelay = Duration.ofSeconds(1);

		/**
		 * Maximum retry delay.
		 */
		private Duration maxDelay = Duration.ofSeconds(30);

		/**
		 * Retry delay multiplier.
		 */
		private Double multiplier = 2.0;
	}

	/**
	 * Producer (outbound) configuration.
	 */
	@Getter
	@Setter
	public static class Producer {

		/**
		 * Default QoS level for published messages.
		 */
		private Integer defaultQos = MqttQoS.AT_MOST_ONCE.value();

		/**
		 * Client ID for the producer. If not specified, a random ID will be generated.
		 */
		private String clientId;

		/**
		 * Default topic for published messages.
		 */
		private String defaultTopic = "producer";

		/**
		 * Whether to publish messages asynchronously.
		 */
		private Boolean async = false;

		/**
		 * Whether to enable async events for message publishing.
		 */
		private Boolean asyncEvents = false;

		/**
		 * Default retained flag for published messages.
		 */
		private Boolean defaultRetained = false;

		/**
		 * Message publish timeout.
		 */
		private Duration publishTimeout = Duration.ofSeconds(30);
	}

	/**
	 * Consumer (inbound) configuration.
	 */
	@Getter
	@Setter
	public static class Consumer {

		/**
		 * QoS level for message subscriptions.
		 */
		private Integer qos = MqttQoS.AT_MOST_ONCE.value();

		/**
		 * Completion timeout for message processing.
		 */
		private Duration completionTimeout = Duration.ofMillis(ClientManager.DEFAULT_COMPLETION_TIMEOUT);

		/**
		 * Whether the consumer should start automatically.
		 */
		private Boolean autoStartup = true;

		/**
		 * Client ID for the consumer. If not specified, a random ID will be generated.
		 */
		private String clientId;

		/**
		 * Whether to process messages asynchronously.
		 */
		private Boolean async = false;

		/**
		 * Whether manual acknowledgments are enabled.
		 */
		private Boolean manualAcks = false;

		/**
		 * Thread pool executor configuration for message processing.
		 */
		private MqttExecutor executor = new MqttExecutor();
	}

	/**
	 * Thread pool executor configuration for MQTT message processing.
	 */
	@Getter
	@Setter
	public static class MqttExecutor {

		/**
		 * Core pool size for the thread pool.
		 */
		private Integer corePoolSize = 5;

		/**
		 * Maximum pool size for the thread pool.
		 */
		private Integer maxPoolSize = 10;

		/**
		 * Keep alive time in seconds for idle threads.
		 */
		private Integer keepAliveSeconds = 60;

		/**
		 * Queue capacity for pending tasks.
		 */
		private Integer queueCapacity = 512;

		/**
		 * Thread name prefix.
		 */
		private String threadNamePrefix = "mqtt-";

		/**
		 * Whether to allow core threads to timeout.
		 */
		private Boolean allowCoreThreadTimeOut = false;
	}
}
