package com.example.productos.application.service;

import io.nats.client.Message;
import com.example.productos.domain.model.Producto;
import com.example.productos.infrastructure.event.NatsEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class ProductoNatsEventListener {

	@Autowired
	private NatsEventPublisher eventPublisher;

//	@Autowired
//	private ProductoService productoService;

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
	        // Obtener la lista de productos
//	        List<Producto> productos = productoService.getItems();
	        List<Producto> productos = null;
	        System.out.println("productos.requestReply: " + productos);

	        // Crear un ObjectMapper para convertir la lista a JSON
	        ObjectMapper objectMapper = new ObjectMapper();

	        // Convertir la lista de productos a JSON
	        String json = objectMapper.writeValueAsString(productos);

	        // Publicar la respuesta utilizando el publisher de eventos
	        eventPublisher.publishResponse(msg, json);
	    } catch (JsonProcessingException e) {
	        // Manejar la excepción de procesamiento de JSON
	        e.printStackTrace();
	    } catch (Exception e) {
	        // Manejar otras excepciones
	        e.printStackTrace();
	    }
	}
	
}
