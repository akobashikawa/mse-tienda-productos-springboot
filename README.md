# mseTienda Productos Spring Boot

Servicio Productos para mseTienda, con Spring Boot, soporta NATS.

## Install

### Postgres

```sh
docker run --name postgres-db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=tienda_productos -p 5432:5432 -d postgres

docker exec -it postgres-db psql -U postgres -d tienda_productos
```

```sql
; psql
\l
CREATE SCHEMA productos;
\dn
exit
```

### NATS

```sh
docker run -d --name nats-server -p 4222:4222 -p 8222:8222 nats -m 8222
```


## Build

### Spring Boot

```sh
# build package without build test
mvn clean package -Dmaven.test.skip=true
```


## Run

### Postgres

```sh
docker start postgres-db
```

### NATS

```sh
docker start nats
```

### Spring Boot

```sh
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tienda_productos \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=postgres \
NATS_URL=nats://localhost:4222 \
mvn spring-boot:run

# test
nats pub productos.test "Hola"
```


## Con Docker y localhost

- `.env`
```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tienda_productos
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
NATS_URL=nats://localhost:4222
```

```sh
docker build -t tienda-productos .

docker run --network="host" \
    -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tienda_productos \
    -e SPRING_DATASOURCE_USERNAME=postgres \
    -e SPRING_DATASOURCE_PASSWORD=postgres \
    -e NATS_URL=nats://localhost:4222 \
    --name tienda-productos \
    tienda-productos

# usando .env
docker run --network="host" --env-file .env --name tienda-productos tienda-productos

```

## Con Docker y network

```sh
docker network create tienda_network

docker run --name db_productos \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=postgres \
    -e POSTGRES_DB=tienda_productos \
    --network tienda_network \
    -p 5432:5432 \
    -d postgres

docker run -d --name nats \
    -p 4222:4222 \
    --network tienda_network \
    nats:latest

docker run -p 8081:8081 \
    -e SPRING_DATASOURCE_URL=jdbc:postgresql://db_productos:5432/tienda_productos \
    -e SPRING_DATASOURCE_USERNAME=postgres \
    -e SPRING_DATASOURCE_PASSWORD=postgres \
    -e NATS_URL=nats://nats:4222 \
    --network tienda_network \
    --name tienda-productos \
    tienda-productos
```

### Con docker-compose

- `Dockerfile` tiene una configuración para desarrollo standalone
- `Dockerfile-mse` tiene una configuración para desarrollo como microservicio, para ser invocado desde la carpeta principal de mseTienda

```sh
docker-compose up --build
docker-compose logs -f tienda-productos
```