package com.libre.mqtt;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.libre.toolkit.json.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class MqttTemplate implements MqttOptions {

	private final MqttPahoMessageDrivenChannelAdapter adapter;

	private final MqttMessageGateWay mqttMessageGateWay;

	@Override
	public void addTopic(String topic) {
		this.addTopic(topic, MqttQoS.AT_MOST_ONCE.value());
	}

	@Override
	public void addTopic(String topic, int qos) {
		Set<String> topics = Sets.newHashSet(adapter.getTopic());
		if (topics.contains(topic)) {
			return;
		}
		adapter.addTopic(topic, qos);
	}

	@Override
	public void removeTopic(String topic) {
		Set<String> topics = Sets.newHashSet(adapter.getTopic());
		if (!topics.contains(topic)) {
			return;
		}
		adapter.removeTopic(topic);
	}

	@Override
	public List<String> listTopics() {
		return Lists.newArrayList(adapter.getTopic());
	}

	@Override
	public void convertAndSend(MqttMessage mqttMessage) {
		try {
			mqttMessageGateWay.sendToMqtt(mqttMessage.getTopic(), mqttMessage.getQos(), mqttMessage.getRetained(),
					JsonUtil.toJson(mqttMessage.getPayload()));
		}
		catch (Exception e) {
			throw new MqttException("Failed to send message, message: " + mqttMessage, e);
		}
	}

}
