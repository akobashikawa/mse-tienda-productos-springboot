package com.example.productos.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.productos.domain.Producto;
import com.example.productos.domain.ProductoRepository;
import com.example.productos.infrastructure.NatsEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductoService {

	private final ProductoRepository productoRepository;

	private final NatsEventPublisher eventPublisher;

	private final ObjectMapper objectMapper;

	public ProductoService(ProductoRepository productoRepository, NatsEventPublisher eventPublisher,
			ObjectMapper objectMapper) {
		this.productoRepository = productoRepository;
		this.eventPublisher = eventPublisher;
		this.objectMapper = objectMapper;
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

	public String handleGet(Map<String, Object> params, Map<String, Object> query) {
		try {
			if (params.containsKey("id")) {
				Long id = Long.parseLong(params.get("id").toString());
				Optional<Producto> productoOpt = getItemById(id);
				if (productoOpt.isPresent()) {
					return objectMapper.writeValueAsString(productoOpt.get()); // Convierte a JSON
				} else {
					Map<String, String> error = new HashMap<>();
					error.put("error", "Producto no encontrado con id: " + id);
					return objectMapper.writeValueAsString(error); // Devuelve error en JSON
				}
			} else {
				List<Producto> productos = getItems();
				return objectMapper.writeValueAsString(productos); // Convierte la lista a JSON
			}
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, String> error = new HashMap<>();
			error.put("error", "Error al procesar la solicitud: " + e.getMessage());
			try {
				return objectMapper.writeValueAsString(error);
			} catch (Exception jsonException) {
				jsonException.printStackTrace();
				return "{\"error\":\"Error al convertir a JSON\"}";
			}
		}
	}

	public String handlePost(Map<String, Object> body) {
		try {
			// Convertir el cuerpo a un objeto Producto
			Producto producto = new Producto();
			producto.setNombre((String) body.get("nombre"));
			producto.setPrecio(Double.parseDouble(body.get("precio").toString()));
			producto.setCosto(Double.parseDouble(body.get("costo").toString()));
			producto.setCantidad(Integer.parseInt(body.get("cantidad").toString()));

			// Crear el producto usando el método ya existente
			Producto nuevoProducto = createItem(producto);

			// Formatear y devolver la respuesta
			return objectMapper.writeValueAsString(nuevoProducto); // Convierte a JSON el producto creado

		} catch (Exception e) {
			e.printStackTrace();
			Map<String, String> error = new HashMap<>();
			error.put("error", "Error al crear el producto: " + e.getMessage());
			try {
				return objectMapper.writeValueAsString(error);
			} catch (Exception jsonException) {
				jsonException.printStackTrace();
				return "{\"error\":\"Error al convertir a JSON\"}";
			}
		}
	}

	public String handlePut(Map<String, Object> params, Map<String, Object> body) {
		try {
			Long id = Long.parseLong(params.get("id").toString());
			Optional<Producto> productoOpt = getItemById(id);

			if (productoOpt.isPresent()) {
				Producto producto = productoOpt.get();
				producto.setNombre((String) body.get("nombre"));
				producto.setPrecio(Double.parseDouble(body.get("precio").toString()));
				producto.setCosto(Double.parseDouble(body.get("costo").toString()));
				producto.setCantidad(Integer.parseInt(body.get("cantidad").toString()));

				// Actualizar el producto usando el método existente
				Producto productoActualizado = updateItem(id, producto);

				return objectMapper.writeValueAsString(productoActualizado); // Convierte a JSON el producto actualizado
			} else {
				Map<String, String> error = new HashMap<>();
				error.put("error", "Producto no encontrado con id: " + id);
				return objectMapper.writeValueAsString(error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, String> error = new HashMap<>();
			error.put("error", "Error al actualizar el producto: " + e.getMessage());
			try {
				return objectMapper.writeValueAsString(error);
			} catch (Exception jsonException) {
				jsonException.printStackTrace();
				return "{\"error\":\"Error al convertir a JSON\"}";
			}
		}
	}

	public String handlePatch(Map<String, Object> params, Map<String, Object> body) {
		try {
			Long id = Long.parseLong(params.get("id").toString());
			Optional<Producto> productoOpt = getItemById(id);

			if (productoOpt.isPresent()) {
				Producto producto = productoOpt.get();

				// Solo actualiza si el campo existe en body
				if (body.containsKey("nombre")) {
					producto.setNombre((String) body.get("nombre"));
				}
				if (body.containsKey("precio")) {
					producto.setPrecio(Double.parseDouble(body.get("precio").toString()));
				}
				if (body.containsKey("costo")) {
					producto.setCosto(Double.parseDouble(body.get("costo").toString()));
				}
				if (body.containsKey("cantidad")) {
					producto.setCantidad(Integer.parseInt(body.get("cantidad").toString()));
				}

				// Actualizar el producto usando el método existente
				Producto productoActualizado = updateItem(id, producto);

				return objectMapper.writeValueAsString(productoActualizado); // Convierte a JSON el producto actualizado
			} else {
				Map<String, String> error = new HashMap<>();
				error.put("error", "Producto no encontrado con id: " + id);
				return objectMapper.writeValueAsString(error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, String> error = new HashMap<>();
			error.put("error", "Error al actualizar el producto: " + e.getMessage());
			try {
				return objectMapper.writeValueAsString(error);
			} catch (Exception jsonException) {
				jsonException.printStackTrace();
				return "{\"error\":\"Error al convertir a JSON\"}";
			}
		}
	}
	
	public String handleDelete(Map<String, Object> params) {
	    try {
	        // Extraer el ID del producto a eliminar
	        Long id = Long.parseLong(params.get("id").toString());
	        
	        // Intentar eliminar el producto usando el método existente
	        deleteItemById(id);

	        // Respuesta de éxito en formato JSON
	        Map<String, String> response = new HashMap<>();
	        response.put("message", "Producto eliminado exitosamente con id: " + id);
	        return objectMapper.writeValueAsString(response); // Convierte a JSON la respuesta de éxito

	    } catch (Exception e) {
	        e.printStackTrace();
	        // Mensaje de error en caso de excepción
	        Map<String, String> error = new HashMap<>();
	        error.put("error", "Error al eliminar el producto: " + e.getMessage());
	        try {
	            return objectMapper.writeValueAsString(error);
	        } catch (Exception jsonException) {
	            jsonException.printStackTrace();
	            return "{\"error\":\"Error al convertir a JSON\"}";
	        }
	    }
	}

}
