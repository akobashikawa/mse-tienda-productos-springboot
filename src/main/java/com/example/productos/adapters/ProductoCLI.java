package com.example.productos.adapters;

import com.example.productos.application.ProductoService;
import com.example.productos.domain.Producto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "producto-cli", description = "CLI para gestionar productos")
public class ProductoCLI implements Runnable {

    @Autowired
    private ProductoService productoService;

    @Option(names = {"-a", "--add"}, description = "Añadir un nuevo producto")
    private boolean addProduct;

    @Parameters(index = "0", description = "Nombre del producto")
    private String nombre;

    @Parameters(index = "1", description = "Costo del producto")
    private Double costo;

    @Parameters(index = "2", description = "Precio del producto")
    private Double precio;
    
    @Parameters(index = "3", description = "Cantidad del producto")
    private Integer cantidad;

    @Override
    public void run() {
        if (addProduct) {
        	Producto producto = new Producto();
        	producto.setNombre(nombre);
        	producto.setCosto(costo);
        	producto.setPrecio(precio);
        	producto.setCantidad(cantidad);
            Producto nuevoProducto = productoService.createItem(producto);
            System.out.println("Producto añadido: " + nuevoProducto);
        }
        // Más comandos aquí
    }
}
