package com.libre.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

import static com.libre.mqtt.MqttProperties.MQTT_INPUT_CHANNEL_NAME;
import static com.libre.mqtt.MqttProperties.MQTT_OUT_BOUND_CHANNEL_NAME;
import static org.zclibre.toolkit.core.StringPool.DASH;
import static org.zclibre.toolkit.core.StringPool.EMPTY;

/**
 * Auto-configuration for MQTT integration with Spring Boot.
 *
 * <p>This configuration class automatically sets up MQTT client connections,
 * message channels, adapters, and handlers based on the provided configuration
 * properties. It supports both MQTT v3.1.1 and v5 protocols with comprehensive
 * customization options.
 *
 * <p>Key components configured:
 * <ul>
 *   <li>MQTT client factory with connection options</li>
 *   <li>Inbound and outbound message channels</li>
 *   <li>Message-driven channel adapter for receiving messages</li>
 *   <li>Message handler for sending messages</li>
 *   <li>Thread pool executor for async message processing</li>
 *   <li>Message converters for JSON serialization</li>
 *   <li>Retry templates for connection resilience</li>
 * </ul>
 *
 * <p>The configuration is activated when:
 * <ul>
 *   <li>{@code libre.mqtt.enabled} is {@code true} (default)</li>
 *   <li>Required MQTT dependencies are on the classpath</li>
 * </ul>
 *
 * <p>Example configuration:
 * <pre>
 * libre:
 *   mqtt:
 *     enabled: true
 *     urls:
 *       - tcp://localhost:1883
 *     username: admin
 *     password: secret
 *     producer:
 *       async: true
 *     consumer:
 *       qos: 1
 * </pre>
 *
 * @author libre-mqtt
 * @since 1.0.0
 * @see MqttProperties
 * @see org.springframework.integration.mqtt.core.MqttPahoClientFactory
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(MqttProperties.class)
@ConditionalOnProperty(prefix = "libre.mqtt", name = "enabled", matchIfMissing = true)
@IntegrationComponentScan(basePackages = "com.libre")
public class MqttAutoConfiguration {

	/**
	 * Creates MQTT connection options based on the provided configuration properties.
	 *
	 * <p>This method configures all aspects of the MQTT connection including:
	 * <ul>
	 *   <li>Server URIs for broker connection and failover</li>
	 *   <li>Authentication credentials (username/password)</li>
	 *   <li>Connection timeouts and keep-alive settings</li>
	 *   <li>SSL/TLS configuration for secure connections</li>
	 *   <li>Automatic reconnection and session management</li>
	 *   <li>WebSocket headers for WebSocket connections</li>
	 * </ul>
	 *
	 * @param mqttProperties the MQTT configuration properties
	 * @return configured MQTT connection options
	 * @see MqttProperties
	 */
	@Bean
	@ConditionalOnMissingBean
	public MqttConnectOptions mqttConnectOptions(MqttProperties mqttProperties) {
		log.debug("Configuring MQTT connection options with {} server URIs",
			mqttProperties.getUrls().length);

		MqttConnectOptions options = new MqttConnectOptions();

		// Server configuration
		options.setServerURIs(mqttProperties.getUrls());
		options.setConnectionTimeout(mqttProperties.getConnectionTimeout());
		options.setKeepAliveInterval(mqttProperties.getKeepAliveInterval());
		options.setMaxInflight(mqttProperties.getMaxInflight());

		// Authentication
		if (StringUtils.hasText(mqttProperties.getUsername())) {
			options.setUserName(mqttProperties.getUsername());
		}
		if (StringUtils.hasText(mqttProperties.getPassword())) {
			options.setPassword(mqttProperties.getPassword().toCharArray());
		}

		// Connection behavior
		options.setAutomaticReconnect(mqttProperties.getAutomaticReconnect());
		options.setCleanSession(mqttProperties.getCleanSession());
		options.setMaxReconnectDelay(mqttProperties.getMaxReconnectDelay());
		options.setExecutorServiceTimeout(mqttProperties.getExecutorServiceTimeout());

		// SSL/TLS configuration
		options.setSSLProperties(mqttProperties.getSslClientProps());
		options.setHttpsHostnameVerificationEnabled(mqttProperties.getHttpsHostnameVerificationEnabled());

		// WebSocket configuration
		options.setCustomWebSocketHeaders(mqttProperties.getCustomWebSocketHeaders());

		// Will message configuration
		if (StringUtils.hasText(mqttProperties.getWillDestination())) {
			// TODO: Configure will message when MqttMessage supports it
			log.debug("Will message destination configured: {}", mqttProperties.getWillDestination());
		}

		log.info("MQTT connection options configured successfully for {} servers",
			mqttProperties.getUrls().length);

		return options;
	}

	/**
	 * Creates the MQTT client factory with configured connection options.
	 *
	 * <p>The client factory is responsible for creating MQTT client instances
	 * with the specified connection configuration. It serves as the foundation
	 * for both inbound and outbound MQTT adapters.
	 *
	 * @param mqttConnectOptions the configured MQTT connection options
	 * @return the MQTT client factory
	 * @see DefaultMqttPahoClientFactory
	 */
	@Bean
	@ConditionalOnMissingBean
	public MqttPahoClientFactory mqttPahoClientFactory(MqttConnectOptions mqttConnectOptions) {
		log.debug("Creating MQTT Paho client factory");

		DefaultMqttPahoClientFactory clientFactory = new DefaultMqttPahoClientFactory();
		clientFactory.setConnectionOptions(mqttConnectOptions);

		log.info("MQTT client factory created successfully");
		return clientFactory;
	}

	/**
	 * Creates the MQTT input channel for receiving messages.
	 *
	 * <p>This channel receives messages from MQTT topics and routes them to
	 * message handlers. The channel type depends on the async configuration:
	 * <ul>
	 *   <li>ExecutorChannel for async processing</li>
	 *   <li>DirectChannel for synchronous processing</li>
	 * </ul>
	 *
	 * @param executor the thread pool executor for async processing
	 * @param properties the MQTT configuration properties
	 * @return the configured input message channel
	 * @see ExecutorChannel
	 * @see DirectChannel
	 */
	@Bean(name = MQTT_INPUT_CHANNEL_NAME)
	@ConditionalOnMissingBean
	public MessageChannel mqttInputChannel(@Qualifier("mqttConsumerExecutor") ThreadPoolTaskExecutor executor,
			MqttProperties properties) {
		MqttProperties.Consumer consumer = properties.getConsumer();

		if (consumer.getAsync()) {
			log.debug("Creating async MQTT input channel with executor");
			return new ExecutorChannel(executor);
		} else {
			log.debug("Creating direct MQTT input channel");
			return new DirectChannel();
		}
	}

	/**
	 * Creates the MQTT outbound channel for sending messages.
	 *
	 * <p>This channel receives messages from the application and routes them
	 * to the MQTT message handler for publishing to MQTT topics.
	 *
	 * @return the outbound message channel
	 * @see DirectChannel
	 */
	@Bean(name = MQTT_OUT_BOUND_CHANNEL_NAME)
	@ConditionalOnMissingBean
	public MessageChannel mqttOutboundChannel() {
		log.debug("Creating MQTT outbound channel");
		return new DirectChannel();
	}

	/**
	 * Creates the MQTT message-driven channel adapter for receiving messages.
	 *
	 * <p>This adapter connects to MQTT topics and converts incoming messages
	 * into Spring Integration messages. It supports:
	 * <ul>
	 *   <li>Automatic client ID generation if not specified</li>
	 *   <li>Configurable QoS levels for message delivery</li>
	 *   <li>Custom message converters for payload transformation</li>
	 *   <li>Completion timeout for message processing</li>
	 *   <li>Auto-startup configuration</li>
	 * </ul>
	 *
	 * @param mqttPahoClientFactory the MQTT client factory
	 * @param properties the MQTT configuration properties
	 * @param mqttInputChannel the input channel for received messages
	 * @param environment the Spring environment for client ID generation
	 * @return the configured message-driven channel adapter
	 * @see MqttPahoMessageDrivenChannelAdapter
	 */
	@Bean
	@ConditionalOnMissingBean
	public MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter(
			MqttPahoClientFactory mqttPahoClientFactory, MqttProperties properties,
			@Qualifier(MQTT_INPUT_CHANNEL_NAME) MessageChannel mqttInputChannel, Environment environment) {

		MqttProperties.Consumer consumer = properties.getConsumer();
		String clientId = consumer.getClientId();
		if (!StringUtils.hasText(clientId)) {
			clientId = getClientId(environment);
			log.debug("Generated MQTT consumer client ID: {}", clientId);
		}

		log.debug("Creating MQTT message-driven channel adapter with client ID: {}", clientId);

		MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId,
				mqttPahoClientFactory);

		// Configure adapter behavior
		adapter.setAutoStartup(consumer.getAutoStartup());
		adapter.setOutputChannel(mqttInputChannel);
		adapter.setQos(consumer.getQos());
		adapter.setCompletionTimeout(consumer.getCompletionTimeout().toMillis());

		// Configure message converter
		adapter.setConverter(new JacksonPahoMessageConverter());

		// Configure manual acknowledgments if enabled
		if (consumer.getManualAcks()) {
			adapter.setManualAcks(true);
			log.debug("Manual acknowledgments enabled for MQTT consumer");
		}

		log.info("MQTT message-driven channel adapter created with client ID: {}", clientId);
		return adapter;
	}

	/**
	 * Creates the MQTT message handler for sending messages.
	 *
	 * <p>This handler receives messages from the outbound channel and publishes
	 * them to MQTT topics. It supports:
	 * <ul>
	 *   <li>Automatic client ID generation if not specified</li>
	 *   <li>Asynchronous message publishing</li>
	 *   <li>Configurable default topic, QoS, and retained flag</li>
	 *   <li>Custom message converters for payload transformation</li>
	 *   <li>Async events for delivery confirmation</li>
	 * </ul>
	 *
	 * @param mqttPahoClientFactory the MQTT client factory
	 * @param mqttProperties the MQTT configuration properties
	 * @param environment the Spring environment for client ID generation
	 * @return the configured MQTT message handler
	 * @see MqttPahoMessageHandler
	 */
	@Bean
	@ServiceActivator(inputChannel = MQTT_OUT_BOUND_CHANNEL_NAME)
	@ConditionalOnMissingBean
	public MqttPahoMessageHandler mqttOutbound(MqttPahoClientFactory mqttPahoClientFactory,
			MqttProperties mqttProperties, Environment environment) {

		MqttProperties.Producer producer = mqttProperties.getProducer();
		String clientId = producer.getClientId();
		if (!StringUtils.hasText(clientId)) {
			clientId = getClientId(environment);
			log.debug("Generated MQTT producer client ID: {}", clientId);
		}

		log.debug("Creating MQTT message handler with client ID: {}", clientId);

		MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId, mqttPahoClientFactory);

		// Configure publishing behavior
		messageHandler.setAsync(producer.getAsync());
		messageHandler.setAsyncEvents(producer.getAsyncEvents());
		messageHandler.setDefaultTopic(producer.getDefaultTopic());
		messageHandler.setDefaultQos(producer.getDefaultQos());
		messageHandler.setDefaultRetained(producer.getDefaultRetained());

		// Configure message converter
		messageHandler.setConverter(new JacksonPahoMessageConverter());

		log.info("MQTT message handler created with client ID: {}", clientId);
		return messageHandler;
	}

	/**
	 * Creates the MQTT options template for dynamic topic management.
	 *
	 * <p>This template provides a high-level API for managing MQTT topics
	 * and sending messages programmatically. It wraps the low-level adapters
	 * with convenient methods for topic subscription and message publishing.
	 *
	 * @param adapter the message-driven channel adapter
	 * @param mqttMessageGateWay the message gateway for sending messages
	 * @return the MQTT options template
	 * @see MqttTemplate
	 */
	@Bean
	@ConditionalOnMissingBean
	public MqttOptions mqttOptions(MqttPahoMessageDrivenChannelAdapter adapter, MqttMessageGateWay mqttMessageGateWay) {
		log.debug("Creating MQTT options template");
		return new MqttTemplate(adapter, mqttMessageGateWay);
	}

	/**
	 * Creates the MQTT message inbound handler for processing received messages.
	 *
	 * <p>This handler routes incoming MQTT messages to registered message listeners
	 * based on topic patterns and filters. It supports:
	 * <ul>
	 *   <li>Topic pattern matching with wildcards</li>
	 *   <li>Shared subscription handling</li>
	 *   <li>Automatic listener registration</li>
	 *   <li>Error handling and logging</li>
	 * </ul>
	 *
	 * @param messageListeners the list of registered message listeners
	 * @param mqttOptions the MQTT options for topic management
	 * @return the configured message inbound handler
	 * @see MqttMessageInboundHandler
	 */
	@Bean
	@ConditionalOnMissingBean
	public MqttMessageInboundHandler mqttMessageInboundHandler(List<MqttMessageListener> messageListeners,
			MqttOptions mqttOptions) {
		log.debug("Creating MQTT message inbound handler with {} listeners", messageListeners.size());
		return new MqttMessageInboundHandler(messageListeners, mqttOptions);
	}

	/**
	 * Creates the thread pool executor for MQTT consumer message processing.
	 *
	 * <p>This executor handles asynchronous processing of incoming MQTT messages
	 * when async mode is enabled. It provides:
	 * <ul>
	 *   <li>Configurable core and maximum pool sizes</li>
	 *   <li>Queue capacity for pending tasks</li>
	 *   <li>Keep-alive time for idle threads</li>
	 *   <li>Caller-runs rejection policy for backpressure</li>
	 *   <li>Descriptive thread naming</li>
	 * </ul>
	 *
	 * @param properties the MQTT configuration properties
	 * @return the configured thread pool executor
	 * @see ThreadPoolTaskExecutor
	 */
	@Bean
	@ConditionalOnMissingBean
	public ThreadPoolTaskExecutor mqttConsumerExecutor(MqttProperties properties) {
		MqttProperties.MqttExecutor executorProperties = properties.getConsumer().getExecutor();

		log.debug("Creating MQTT consumer executor with core pool size: {}, max pool size: {}",
			executorProperties.getCorePoolSize(), executorProperties.getMaxPoolSize());

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(executorProperties.getCorePoolSize());
		executor.setMaxPoolSize(executorProperties.getMaxPoolSize());
		executor.setQueueCapacity(executorProperties.getQueueCapacity());
		executor.setKeepAliveSeconds(executorProperties.getKeepAliveSeconds());
		executor.setAllowCoreThreadTimeOut(executorProperties.getAllowCoreThreadTimeOut());
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.setThreadNamePrefix(executorProperties.getThreadNamePrefix());
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(properties.getExecutorServiceTimeout());
		executor.initialize();

		log.info("MQTT consumer executor created successfully");
		return executor;
	}

	/**
	 * Creates the MQTT retry template for connection resilience.
	 *
	 * <p>This template provides retry logic for MQTT operations that may fail
	 * due to network issues or broker unavailability. It uses exponential
	 * backoff strategy to avoid overwhelming the broker.
	 *
	 * @param properties the MQTT configuration properties
	 * @return the configured retry template
	 * @see MqttRetryTemplate
	 */
	@Bean
	@ConditionalOnMissingBean
	public MqttRetryTemplate mqttRetryTemplate(MqttProperties properties) {
		MqttProperties.Retry retryConfig = properties.getRetry();

		if (!retryConfig.getEnabled()) {
			log.debug("MQTT retry is disabled, creating default template");
			return MqttRetryTemplate.defaultTemplate();
		}

		log.debug("Creating MQTT retry template with {} max attempts", retryConfig.getMaxAttempts());

		return new MqttRetryTemplate(
			retryConfig.getMaxAttempts(),
			retryConfig.getInitialDelay(),
			retryConfig.getMaxDelay(),
			retryConfig.getMultiplier()
		);
	}

	/**
	 * Generates a unique client ID for MQTT connections.
	 *
	 * <p>The client ID is constructed using the Spring application name
	 * (if available) combined with a UUID to ensure uniqueness across
	 * multiple application instances.
	 *
	 * @param environment the Spring environment
	 * @return a unique MQTT client ID
	 */
	private String getClientId(Environment environment) {
		String applicationName = environment.getProperty("spring.application.name", "libre-mqtt");
		String uniqueId = UUID.randomUUID().toString().replace(DASH, EMPTY);
		String clientId = applicationName + DASH + uniqueId;

		log.debug("Generated MQTT client ID: {}", clientId);
		return clientId;
	}
}
