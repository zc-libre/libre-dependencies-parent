package com.libre.rabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author: Libre
 * @Date: 2023/4/9 7:37 AM
 */
@RestController
public class TestController {

	@Autowired
	private MessageReceiver messageReceiver;

	@GetMapping("/send")
	public String sendMessageAndReceiveResponse(@RequestParam String content) {
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
