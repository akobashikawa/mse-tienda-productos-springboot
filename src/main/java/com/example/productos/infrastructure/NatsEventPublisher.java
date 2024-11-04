package com.example.productos.infrastructure;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import io.nats.client.MessageHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class NatsEventPublisher {

	private final Connection natsConnection;
	private final ObjectMapper objectMapper;

	public NatsEventPublisher(Connection natsConnection) {
		this.natsConnection = natsConnection;
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
		this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	public void publishResponse(Message msg, String json) {
		try {
			natsConnection.publish(msg.getReplyTo(), json.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void publishEvent(String subject, Object payload) {
		try {
			String message = objectMapper.writeValueAsString(payload);
			natsConnection.publish(subject, message.getBytes());
			System.out.println("Evento NATS publicado " + subject + ": " + message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void subscribeToEvent(String topic, MessageHandler handler) {
		try {
			Dispatcher dispatcher = natsConnection.createDispatcher(handler);
			dispatcher.subscribe(topic);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Map<String, Object> getPayload(Message msg) {
		String data = new String(msg.getData());
		try {
			return objectMapper.readValue(data, Map.class);
		} catch (Exception e) {
			return null;
		}
	}
}
