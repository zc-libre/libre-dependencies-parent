package com.libre.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author: Libre
 * @Date: 2023/4/9 6:43 AM
 */
@SpringBootApplication(scanBasePackages = "com.libre.rabbitmq")
public class RabbitMqApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitMqApplication.class, args);
	}

	@Bean
	public Queue responseQueue() {
		return new Queue("responseQueue");
	}

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange("exchange");
	}

	@Bean
	public Binding binding1(Queue requestQueue, DirectExchange exchange) {
		return BindingBuilder.bind(requestQueue).to(exchange).with("request");
	}

	@Bean
	public Binding binding2(Queue responseQueue, DirectExchange exchange) {
		return BindingBuilder.bind(responseQueue).to(exchange).with("response");
	}

	// 提供自定义RabbitTemplate,将对象序列化为json串
	@Bean
	public RabbitTemplate jacksonRabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
		return rabbitTemplate;
	}

}
