package com.libre.rabbitmq;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Message implements Serializable {

	@Serial
	private static final long serialVersionUID = 42L;

	private String id;

	private String content;

	public Message() {
	}

	public Message(String id, String content) {
		this.id = id;
		this.content = content;
	}

	// Getter、Setter方法省略

	@Override
	public String toString() {
		return "Message{" + "id='" + id + '\'' + ", content='" + content + '\'' + '}';
	}

}
