package com.libre.mqtt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MqttTest {

	@Autowired
	private MqttMessageGateWay mqttMessageGateWay;

	@Test
	void publish() {
		mqttMessageGateWay.sendToMqtt("test", "123");
	}

}
