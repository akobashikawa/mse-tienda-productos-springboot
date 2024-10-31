# Usar una imagen base con Maven y JDK
FROM maven:3.8.5-openjdk-17 AS build

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar los archivos de configuración y el código fuente
COPY pom.xml ./
COPY src ./src
COPY public ./public
COPY .env ./.env

# Descargar dependencias sin ejecutar el código
RUN mvn dependency:go-offline

# Exponer el puerto en el que correrá la aplicación
EXPOSE 8081

# Ejecutar la aplicación en modo desarrollo
CMD ["mvn", "spring-boot:run"]
