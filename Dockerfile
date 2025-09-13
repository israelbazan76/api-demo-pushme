# -----------------------
# Fase 1: Build con Maven
# -----------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Instalar Node.js 18 (compatible con Vaadin 24.x)
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
  && apt-get install -y nodejs

# Directorio de trabajo
WORKDIR /app

# Copiar pom.xml y descargar dependencias primero (cache eficiente)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Construir la aplicación en modo producción (frontend compilado)
RUN mvn clean package -Pproduction -DskipTests

# -----------------------
# Fase 2: Imagen final
# -----------------------
FROM eclipse-temurin:17-jdk

# Directorio de trabajo
WORKDIR /app

# Copiar el JAR desde la fase build
COPY --from=build /app/target/demopushme-0.0.1-SNAPSHOT.jar app.jar

# Exponer puerto
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java","-jar","app.jar"]
