# Microservicio Productos

## Postgres

```sh
docker run --name postgres-db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=tienda -p 5432:5432 -d postgres

docker exec -it postgres-db psql -U postgres -d tienda
```

```sql
CREATE SCHEMA productos;
CREATE SCHEMA personas;
CREATE SCHEMA ventas;
```

## Mongo

```sh
docker run --name mongodb -p 27017:27017 -d mongodb/mongodb-community-server:latest
```
