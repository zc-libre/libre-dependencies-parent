package com.libre.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.integration.mqtt.event.*;
import org.springframework.stereotype.Component;

/**
 * MQTT connection and event monitor that listens to various MQTT integration events.
 *
 * <p>
 * This component provides comprehensive monitoring of MQTT connection status, message
 * delivery, subscription changes, and error conditions. It logs important events and can
 * be extended to provide metrics or alerting capabilities.
 *
 * <p>
 * Monitored events include:
 * <ul>
 * <li>Connection establishment and failures</li>
 * <li>Message sending and delivery confirmation</li>
 * <li>Topic subscription and unsubscription</li>
 * <li>General MQTT integration events</li>
 * </ul>
 *
 * <p>
 * Example usage: <pre>
 * // The monitor is automatically registered as a Spring component
 * // and will start listening to MQTT events when the application starts
 * </pre>
 *
 * @author libre-mqtt
 * @see org.springframework.integration.mqtt.event.MqttIntegrationEvent
 * @since 1.0.0
 */
@Slf4j
@Component
public class MqttConnectionMonitor {

	/**
	 * Handles MQTT connection failure events.
	 * @param event the connection failure event
	 */
	@EventListener
	public void handleConnectionFailure(MqttConnectionFailedEvent event) {
		log.error("MQTT connection failed: source={}", event.getSource(), event.getCause());
		// Add metrics collection
		// Add alerting logic
	}

	/**
	 * Handles MQTT message sent events.
	 * @param event the message sent event
	 */
	@EventListener
	public void handleMessageSent(MqttMessageSentEvent event) {
		log.debug("MQTT message sent: messageId={}, topic={}", event.getMessageId(), event.getMessage());
		// Add message sending metrics
	}

	/**
	 * Handles MQTT message delivered events.
	 * @param event the message delivered event
	 */
	@EventListener
	public void handleMessageDelivered(MqttMessageDeliveredEvent event) {
		log.debug("MQTT message delivered: messageId={}", event.getMessageId());

		// delivery confirmation metrics
	}

	/**
	 * Handles MQTT topic subscription events.
	 * @param event the subscription event
	 */
	@EventListener
	public void handleSubscription(MqttSubscribedEvent event) {
		log.info("MQTT topic subscribed: message={}", event.getMessage());
		// subscription metrics
	}

	/**
	 * Handles general MQTT integration events.
	 * @param event the MQTT integration event
	 */
	@EventListener
	public void handleMqttEvent(MqttIntegrationEvent event) {
		log.debug("MQTT integration event: type={}, source={}", event.getClass().getSimpleName(), event.getSource());
		// Add general event metrics
	}

}
