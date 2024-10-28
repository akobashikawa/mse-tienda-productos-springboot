package com.example.productos.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.productos.infrastructure.event.NatsEventPublisher;
import com.example.productos.domain.model.Producto;
import com.example.productos.domain.repository.ProductoRepository;

@Service
public class ProductoService {

	private final ProductoRepository productoRepository;
	
	private final NatsEventPublisher eventPublisher;

	public ProductoService(ProductoRepository productoRepository, NatsEventPublisher eventPublisher) {
		this.productoRepository = productoRepository;
		this.eventPublisher = eventPublisher;
	}

	public List<Producto> getItems() {
		return productoRepository.findAll();
	}

	public Optional<Producto> getItemById(Long id) {
		return productoRepository.findById(id);
	}

	public Producto createItem(Producto producto) {
		Producto newItem = productoRepository.save(producto);
		Map<String, Object> payload = new HashMap<>();
		payload.put("producto", newItem);
		eventPublisher.publishEvent("producto.created", payload);
		return newItem;
	}

	public Producto updateItem(Long id, Producto producto) {
		Producto found = productoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
		if (producto.getNombre() != null) {
			found.setNombre(producto.getNombre());
		}
		if (producto.getCosto() != null) {
			found.setCosto(producto.getCosto());
		}
		if (producto.getPrecio() != null) {
			found.setPrecio(producto.getPrecio());
		}
		if (producto.getCantidad() != null) {
			found.setCantidad(producto.getCantidad());
		}
		Producto updatedItem = productoRepository.save(found);
		Map<String, Object> payload = new HashMap<>();
		payload.put("producto", updatedItem);
		eventPublisher.publishEvent("producto.updated", payload);
		return updatedItem;
	}

	public void deleteItemById(Long id) {
		if (!productoRepository.existsById(id)) {
	        throw new RuntimeException("Producto con ID " + id + " no encontrado");
	    }
	    productoRepository.deleteById(id);
	}

	public void decProductoCantidad(Producto producto, int cantidad, int cantidadAnterior) {
        if (producto.getCantidad() < cantidad) {
            throw new RuntimeException("Cantidad insuficiente para el producto " + producto.getId());
        }
        
        int diferencia = cantidad - cantidadAnterior;
        int nuevaCantidad = producto.getCantidad() - diferencia;
        producto.setCantidad(nuevaCantidad);
        updateItem(producto.getId(), producto);
    }
    
    public void decProductoCantidad(Producto producto, int cantidad) {
    	decProductoCantidad(producto, cantidad, 0);
    }
	
}
