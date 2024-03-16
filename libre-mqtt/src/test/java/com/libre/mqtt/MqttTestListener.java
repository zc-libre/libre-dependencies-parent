package com.libre.mqtt;

@MqttListener(topic = "test")
public class MqttTestListener implements MqttMessageListener {

	@Override
	public void onMessage(MqttMessage message) {
		System.out.println(message);
	}

}
