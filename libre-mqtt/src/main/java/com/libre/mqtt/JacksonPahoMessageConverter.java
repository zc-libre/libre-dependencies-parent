package com.libre.mqtt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.libre.toolkit.json.JsonUtil;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;

import java.util.Objects;

public class JacksonPahoMessageConverter extends DefaultPahoMessageConverter {

	@Override
	protected Object mqttBytesToPayload(MqttMessage mqttMessage) {
		// @formatter:off
		return Objects.requireNonNull(JsonUtil.readValue(mqttMessage.getPayload(), new TypeReference<>() {}));
		// @formatter:on
	}

	@Override
	protected byte[] messageToMqttBytes(Message<?> message) {
		Object payload = message.getPayload();
		return JsonUtil.toJsonAsBytes(payload);
	}

}
