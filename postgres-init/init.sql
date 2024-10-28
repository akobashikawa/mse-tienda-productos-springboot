CREATE SCHEMA IF NOT EXISTS productos;

CREATE TABLE productos.productos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    costo NUMERIC(10, 2),
    precio NUMERIC(10, 2),
    cantidad INTEGER
);
