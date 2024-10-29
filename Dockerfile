FROM eclipse-temurin:11
RUN apt-get update && apt-get install -y inotify-tools dos2unix && apt-get clean

# Establecer el directorio de trabajo
WORKDIR /app

# Copia el código fuente y el wrapper de Maven (mvnw)
COPY . .

# Descargar dependencias sin ejecutar el código
RUN mvn dependency:go-offline

# Construir el proyecto (esto generará el WAR)
# RUN mvn clean package -Dmaven.test.skip=true

# Exponer el puerto en el que correrá la aplicación
EXPOSE 8081
EXPOSE 35729
EXPOSE 5005

# Ejecutar la aplicación en modo desarrollo
# CMD ["mvn", "spring-boot:run"]
# CMD ["java", "-javaagent:libs/springloaded.jar", "-jar", "target/productos-0.0.1-SNAPSHOT.war"]
# CMD ["java", "-Dspring.devtools.livereload.enabled=true", "-jar", "target/productos-0.0.1-SNAPSHOT.war"]
# Asegura que el wrapper de Maven (mvnw) tenga permisos ejecutables
RUN dos2unix mvnw && chmod +x mvnw
