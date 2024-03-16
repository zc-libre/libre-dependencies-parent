package com.libre.mqtt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class MqttMessageInboundHandler implements MessageHandler, InitializingBean {

	private final Map<String, MqttMessageListener> mqttMessageListenerContext = new ConcurrentHashMap<>();

	private final List<MqttMessageListener> mqttMessageListenerList;

	private final MqttOptions mqttOptions;

	@Override
	@ServiceActivator(inputChannel = MqttProperties.MQTT_INPUT_CHANNEL_NAME)
	public void handleMessage(Message<?> message) throws MessagingException {
		log.debug("message arrived from server, message: {}", message);
		MqttMessage mqttMessage = new MqttMessage(message);
		for (String topicFilter : mqttMessageListenerContext.keySet()) {
			if (TopicUtils.isTopicFilter(topicFilter) && TopicUtils.match(topicFilter, mqttMessage.getTopic())) {
				MqttMessageListener mqttMessageListener = mqttMessageListenerContext.get(topicFilter);
				mqttMessageListener.onMessage(mqttMessage);
			}
			else if (TopicFilterType.SHARE.match(topicFilter, mqttMessage.getTopic())) {
				MqttMessageListener mqttMessageListener = mqttMessageListenerContext.get(topicFilter);
				mqttMessageListener.onMessage(mqttMessage);
			}
			else if (topicFilter.equals(mqttMessage.getTopic())) {
				MqttMessageListener mqttMessageListener = mqttMessageListenerContext.get(topicFilter);
				mqttMessageListener.onMessage(mqttMessage);
			}
			else {
				log.error("Topic filter match failed, topic: {}, message: {}", topicFilter, mqttMessage);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (CollectionUtils.isEmpty(mqttMessageListenerList)) {
			return;
		}
		for (MqttMessageListener mqttMessageListener : mqttMessageListenerList) {
			Class<?> clazz = ClassUtils.getUserClass(mqttMessageListener);
			MqttListener mqttListener = AnnotationUtils.findAnnotation(clazz, MqttListener.class);
			if (Objects.isNull(mqttListener)) {
				continue;
			}
			String topic = mqttListener.topic();
			mqttOptions.addTopic(topic, mqttListener.qos());
			mqttMessageListenerContext.put(topic, mqttMessageListener);
			log.debug("register topic listener {} success，topic: {}", mqttMessageListener.getClass().getName(), topic);
		}
	}

}
