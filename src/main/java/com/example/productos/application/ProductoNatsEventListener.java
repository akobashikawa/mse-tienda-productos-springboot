package com.example.productos.application;

import io.nats.client.Message;

import com.example.productos.domain.Producto;
import com.example.productos.infrastructure.NatsEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class ProductoNatsEventListener {

	@Autowired
	private NatsEventPublisher eventPublisher;

	@Autowired
	private ProductoService productoService;

	@PostConstruct
	public void init() throws Exception {
		eventPublisher.subscribeToEvent("productos.test", this::handleProductosTestEvent);
		eventPublisher.subscribeToEvent("productos.requestReply", this::handleProductosRequestReply);
	}

	private void handleProductosTestEvent(Message msg) {
		try {
			System.out.println("productos.test: OK");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleProductosRequestReply(Message msg) {
		try {
			Map<String, Object> request = eventPublisher.getPayload(msg);
			String method = (String) request.get("method");
			Map<String, Object> params = (Map<String, Object>) request.get("params");
			Map<String, Object> query = (Map<String, Object>) request.get("query");
			Map<String, Object> body = (Map<String, Object>) request.get("body");
			System.out.println(method);
			System.out.println(params);
			System.out.println(query);
			System.out.println(body);
			
			String response;

			switch (method.toUpperCase()) {
			case "GET":
				response = productoService.handleGet(params, query);
				break;
			case "POST":
				response = productoService.handlePost(body);
				break;
			case "PUT":
				response = productoService.handlePut(params, body);
				break;
			case "PATCH":
				response = productoService.handlePatch(params, body);
				break;
			case "DELETE":
				response = productoService.handleDelete(params);
				break;
			default:
				response = "Unsopported method: " + method;
			}
			
			// Publicar la respuesta o responder a la solicitud
	        msg.getConnection().publish(msg.getReplyTo(), response.getBytes());
		} catch (Exception e) {
			// Manejar otras excepciones
			e.printStackTrace();
		}
	}

}
