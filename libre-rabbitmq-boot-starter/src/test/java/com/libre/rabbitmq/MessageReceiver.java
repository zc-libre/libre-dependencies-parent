package com.libre.rabbitmq;

import com.libre.toolkit.json.JsonUtil;
import com.rabbitmq.client.Channel;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageReceiver {

    private final ConcurrentHashMap<String, CompletableFuture<Message>> resultMap = new ConcurrentHashMap<>();

    @Autowired
    private MessageSender messageSender;

    @PostConstruct
    private void init() {
        messageSender.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("消息已经发送到RabbitMQ，correlationId=" + correlationData.getId());
            } else {
                System.out.println("消息发送到RabbitMQ失败，correlationId=" + correlationData.getId() + "，原因：" + cause);
            }
        });
    }

    @RabbitListener(queues = "requestQueue")
    public void receiveMessage(String json, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
		Message message = JsonUtil.readValue(json, Message.class);
		String messageId = message.getId();
        CompletableFuture<Message> future = resultMap.remove(messageId);
        if (future != null) {
			message.setContent("world");
			future.complete(message);
        }
        channel.basicAck(tag, false);
    }

    public CompletableFuture<Message> sendAndReceive(Message message) {
        CompletableFuture<Message> future = new CompletableFuture<>();
        String messageId = UUID.randomUUID().toString();
        resultMap.put(messageId, future);
        message.setId(messageId);
        messageSender.sendMessage(message);
        return future;
    }
}
