package com.libre.rabbitmq;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MessageSender {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void sendMessage(Message message) {
		rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
		CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
		rabbitTemplate.convertAndSend("exchange", "request", message, correlationData);
	}

	public void setConfirmCallback(RabbitTemplate.ConfirmCallback confirmCallback) {
		rabbitTemplate.setConfirmCallback(confirmCallback);
	}

}
