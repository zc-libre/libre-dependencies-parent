package com.libre.rabbitmq;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author: Libre
 * @Date: 2023/4/9 6:44 AM
 */
@SpringBootTest
public class RabbitTest {

	@Autowired
	private MessageReceiver messageReceiver;

	@Test
	void send() {
		String result = sendMessageAndReceiveResponse("hello");
		System.out.println(result);
	}

	public String sendMessageAndReceiveResponse(String content) {
		Message message = new Message();
		message.setContent(content);
		CompletableFuture<Message> future = messageReceiver.sendAndReceive(message);
		try {
			Message responseMessage = future.get();
			return responseMessage.getContent();
		}
		catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

}
