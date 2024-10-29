# Usar una imagen base de JDK
FROM eclipse-temurin:17-jdk-alpine

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el JAR construido a la imagen
COPY target/productos-0.0.1-SNAPSHOT.war app.war

# Exponer el puerto en el que corre la aplicación
EXPOSE 8081

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.war"]
