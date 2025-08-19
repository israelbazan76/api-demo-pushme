# Usa una imagen oficial de Java con Maven incluido
FROM maven:3.8.6-eclipse-temurin-17-alpine AS builder

# Directorio de trabajo para construir la app
WORKDIR /app

# Copia el archivo de dependencias primero (aprovecha la cache de Docker)
COPY pom.xml .
# Descarga las dependencias
RUN mvn dependency:go-offline

# Copia todo el código fuente
COPY src ./src

# Compila el proyecto y crea el JAR
RUN mvn clean package -DskipTests

# --- Segunda etapa: crea una imagen más pequeña solo para ejecutar ---
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia el JAR desde la etapa de construcción
COPY --from=builder /app/target/*.jar app.jar

# Expone el puerto (Render lo manejará con la variable PORT)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]