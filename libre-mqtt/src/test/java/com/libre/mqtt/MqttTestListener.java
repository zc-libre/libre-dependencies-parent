package com.libre.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.integration.mqtt.event.MqttSubscribedEvent;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@MqttListener(topic = "test")
public class MqttTestListener implements MqttMessageListener {

	private AtomicInteger integer = new AtomicInteger(1);

	@Override
	public void onMessage(MqttMessage message) {
		System.err.println(message);
		// log.info("接收到消息, topic: {}, message: {}", message.getTopic(), message);
		int andIncrement = integer.getAndIncrement();
		System.err.println(andIncrement);
	}

	@EventListener(MqttSubscribedEvent.class)
	public void onEvent(MqttSubscribedEvent event) {
		String message = event.getMessage();
		log.info("event: {}", event);
	}

}
