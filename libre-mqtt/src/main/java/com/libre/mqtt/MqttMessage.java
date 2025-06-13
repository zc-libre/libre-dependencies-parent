package com.libre.mqtt;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.io.Serializable;
import java.util.Objects;

/**
 * MQTT message wrapper that encapsulates message content and metadata.
 *
 * <p>This class provides a convenient way to work with MQTT messages in Spring Integration,
 * wrapping both the payload and MQTT-specific headers like topic, QoS, and retained flag.
 *
 * <p>Example usage:
 * <pre>
 * // Create a simple message
 * MqttMessage message = new MqttMessage("Hello World", "sensors/temperature");
 *
 * // Create a message with QoS
 * MqttMessage message = new MqttMessage("Hello World", "sensors/temperature", 1);
 *
 * // Create from Spring Integration Message
 * MqttMessage message = MqttMessage.of(springMessage);
 * </pre>
 *
 * @author libre-mqtt
 * @since 1.0.0
 * @see org.springframework.integration.mqtt.support.MqttHeaders
 */
@Data
@NoArgsConstructor
public class MqttMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Spring Integration message headers.
	 */
	private MessageHeaders messageHeaders;

	/**
	 * Message payload content.
	 */
	private Object payload;

	/**
	 * MQTT topic name.
	 */
	private String topic;

	/**
	 * Quality of Service level (0, 1, or 2).
	 */
	private Integer qos;

	/**
	 * Whether the message should be retained by the broker.
	 */
	private Boolean retained;

	/**
	 * Creates an MQTT message from a Spring Integration Message.
	 *
	 * @param message the Spring Integration message
	 * @throws NullPointerException if required MQTT headers are missing
	 */
	public MqttMessage(Message<?> message) {
		this.messageHeaders = message.getHeaders();
		this.topic = (String) Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC));
		this.qos = (Integer) Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_QOS));
		this.retained = (Boolean) Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_RETAINED));
		this.payload = message.getPayload();
	}

	/**
	 * Creates an MQTT message with default QoS (0) and retained flag (false).
	 *
	 * @param payload the message payload
	 * @param topic the MQTT topic
	 */
	public MqttMessage(Object payload, String topic) {
		this.payload = payload;
		this.topic = topic;
		this.qos = MqttQoS.AT_MOST_ONCE.value();
		this.retained = false;
	}

	/**
	 * Creates an MQTT message with specified QoS and default retained flag (false).
	 *
	 * @param payload the message payload
	 * @param topic the MQTT topic
	 * @param qos the Quality of Service level (0, 1, or 2)
	 */
	public MqttMessage(Object payload, String topic, Integer qos) {
		this.payload = payload;
		this.topic = topic;
		this.qos = qos;
		this.retained = false;
	}

	/**
	 * Creates an MQTT message with all parameters specified.
	 *
	 * @param payload the message payload
	 * @param topic the MQTT topic
	 * @param qos the Quality of Service level (0, 1, or 2)
	 * @param retained whether the message should be retained by the broker
	 */
	public MqttMessage(Object payload, String topic, Integer qos, Boolean retained) {
		this.payload = payload;
		this.topic = topic;
		this.qos = qos;
		this.retained = retained;
	}

	/**
	 * Factory method to create an MQTT message from a Spring Integration Message.
	 *
	 * @param message the Spring Integration message
	 * @return a new MqttMessage instance
	 * @throws NullPointerException if required MQTT headers are missing
	 */
	public static MqttMessage of(Message<?> message) {
		return new MqttMessage(message);
	}

	/**
	 * Factory method to create an MQTT message with specified parameters.
	 *
	 * @param payload the message payload
	 * @param topic the MQTT topic
	 * @param qos the Quality of Service level
	 * @return a new MqttMessage instance
	 */
	public static MqttMessage of(Object payload, String topic, Integer qos) {
		return new MqttMessage(payload, topic, qos);
	}

}
