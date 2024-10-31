package com.example.productos.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.productos.domain.Producto;

public interface JpaProductoRepository extends JpaRepository<Producto, Long> {
}
