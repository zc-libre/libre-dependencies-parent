package com.libre.mqtt;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@MqttListener(topic = "test")
public class MqttTestListener implements MqttMessageListener {

	@Override
	public void onMessage(MqttMessage message) {
		log.info("接收到消息, topic: {}, message: {}", message.getTopic(), message);
	}

}
