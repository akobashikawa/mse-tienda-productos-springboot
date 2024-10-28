package com.example.productos.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.productos.domain.model.Producto;

public interface JpaProductoRepository extends JpaRepository<Producto, Long> {
}
