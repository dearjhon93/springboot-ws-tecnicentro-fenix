# Paso 1: Compilar la aplicación usando Maven con Java 17 (o cambia a 21 si la usas)
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Paso 2: Crear la imagen de ejecución ligera
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Cloud Run requiere que la aplicación escuche en el puerto que le asigne la variable $PORT
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
