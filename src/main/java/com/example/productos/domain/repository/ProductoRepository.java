package com.example.productos.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.productos.domain.model.Producto;

public interface ProductoRepository {
	List<Producto> findAll();

	Optional<Producto> findById(Long id);

	Producto save(Producto producto);

	void deleteById(Long id);

	boolean existsById(Long id);
}
