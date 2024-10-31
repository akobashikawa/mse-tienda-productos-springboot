package com.example.productos.infrastructure;

import org.springframework.stereotype.Repository;

import com.example.productos.domain.Producto;
import com.example.productos.domain.ProductoRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductoJpaRepository implements ProductoRepository {

    private final JpaProductoRepository jpaProductoRepository;

    public ProductoJpaRepository(JpaProductoRepository jpaProductoRepository) {
        this.jpaProductoRepository = jpaProductoRepository;
    }

    @Override
    public List<Producto> findAll() {
        return jpaProductoRepository.findAll();
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return jpaProductoRepository.findById(id);
    }

    @Override
    public Producto save(Producto producto) {
        return jpaProductoRepository.save(producto);
    }

    @Override
    public void deleteById(Long id) {
        jpaProductoRepository.deleteById(id);
    }

	@Override
	public boolean existsById(Long id) {
		return jpaProductoRepository.existsById(id);
	}
}
