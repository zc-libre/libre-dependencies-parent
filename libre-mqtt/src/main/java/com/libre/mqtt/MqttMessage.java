package com.libre.mqtt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MqttMessage implements Serializable {

	private MessageHeaders messageHeaders;

	private Object payload;

	private String topic;

	private Integer qos;

	public MqttMessage(Message<?> message) {
		this.messageHeaders = message.getHeaders();
		this.topic = (String) Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC));
		this.qos = (Integer) Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_QOS));
		this.payload = message.getPayload();
	}

	public MqttMessage(Object payload, String topic, Integer qos) {
		this.payload = payload;
		this.topic = topic;
		this.qos = qos;
	}

	public static MqttMessage of(Object payload, String topic, Integer qos) {
		return new MqttMessage(payload, topic, qos);
	}

}
