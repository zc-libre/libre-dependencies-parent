package com.libre.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.zclibre.toolkit.json.JsonUtil;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;

/**
 * Enhanced MQTT message converter that uses Jackson for JSON serialization/deserialization.
 *
 * <p>This converter extends the default Paho message converter to provide JSON-based
 * message serialization, making it easier to work with complex object payloads in
 * MQTT messages. It handles both inbound and outbound message conversion automatically.
 *
 * <p>Features:
 * <ul>
 *   <li>Automatic JSON serialization for outbound messages</li>
 *   <li>Automatic JSON deserialization for inbound messages</li>
 *   <li>Fallback to string conversion for non-JSON payloads</li>
 *   <li>Comprehensive error handling and logging</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>
 * &#64;Bean
 * public MqttPahoMessageConverter messageConverter() {
 *     return new JacksonPahoMessageConverter();
 * }
 * </pre>
 *
 * @author libre-mqtt
 * @since 1.0.0
 * @see org.springframework.integration.mqtt.support.DefaultPahoMessageConverter
 */
@Slf4j
public class JacksonPahoMessageConverter extends DefaultPahoMessageConverter {

	/**
	 * Converts MQTT message bytes to a Java object payload.
	 *
	 * <p>This method attempts to deserialize the MQTT message payload as JSON.
	 * If JSON deserialization fails, it falls back to string conversion.
	 *
	 * @param mqttMessage the MQTT message containing the payload bytes
	 * @return the converted payload object
	 * @throws MessageConversionException if conversion fails
	 */
	@Override
	protected Object mqttBytesToPayload(org.eclipse.paho.client.mqttv3.MqttMessage mqttMessage) {
		Assert.notNull(mqttMessage, "MQTT message must not be null");

		byte[] payload = mqttMessage.getPayload();
		if (payload == null || payload.length == 0) {
			log.debug("Received empty MQTT message payload");
			return "";
		}

		try {
			// First try to parse as JSON
			String jsonString = new String(payload, StandardCharsets.UTF_8);
			if (isJsonString(jsonString)) {
				Object result = JsonUtil.readValue(jsonString, Object.class);
				log.debug("Successfully converted MQTT payload to JSON object: {}", result.getClass().getSimpleName());
				return result;
			} else {
				// Fallback to string if not JSON
				log.debug("MQTT payload is not JSON, returning as string");
				return jsonString;
			}
		} catch (Exception e) {
			log.warn("Failed to convert MQTT payload to JSON, falling back to string: {}", e.getMessage());
			return new String(payload, StandardCharsets.UTF_8);
		}
	}

	/**
	 * Converts a Spring Integration message to MQTT message bytes.
	 *
	 * <p>This method serializes the message payload to JSON bytes. For string payloads,
	 * it uses UTF-8 encoding directly. For other object types, it uses JSON serialization.
	 *
	 * @param message the Spring Integration message
	 * @return the serialized message bytes
	 * @throws MessageConversionException if conversion fails
	 */
	@Override
	protected byte[] messageToMqttBytes(Message<?> message) {
		Assert.notNull(message, "Message must not be null");

		Object payload = message.getPayload();

		try {
			if (payload instanceof String stringPayload) {
				// Direct string conversion
				log.debug("Converting string payload to bytes: {} characters", stringPayload.length());
				return stringPayload.getBytes(StandardCharsets.UTF_8);
			} else if (payload instanceof byte[]) {
				// Direct byte array
				log.debug("Payload is already byte array: {} bytes", ((byte[]) payload).length);
				return (byte[]) payload;
			} else {
				// JSON serialization for objects
				String jsonString = JsonUtil.toJson(payload);
				log.debug("Converted object payload to JSON: {} -> {} characters",
					payload.getClass().getSimpleName(), jsonString.length());
				return jsonString.getBytes(StandardCharsets.UTF_8);
			}
		} catch (Exception e) {
			String errorMsg = String.format("Failed to convert message payload to bytes: %s", e.getMessage());
			log.error(errorMsg, e);
			throw new MessageConversionException(errorMsg, e);
		}
	}

	/**
	 * Checks if a string appears to be JSON format.
	 *
	 * @param str the string to check
	 * @return true if the string appears to be JSON
	 */
	private boolean isJsonString(String str) {
		if (str.trim().isEmpty()) {
			return false;
		}

		String trimmed = str.trim();
		return (trimmed.startsWith("{") && trimmed.endsWith("}")) ||
			   (trimmed.startsWith("[") && trimmed.endsWith("]"));
	}
}
