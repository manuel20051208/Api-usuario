FROM eclipse-temurin:23-jdk

WORKDIR /app
COPY . .

# Da permisos al wrapper de Maven
RUN chmod +x mvnw

# Compila el proyecto sin ejecutar tests
RUN ./mvnw -B clean package -DskipTests

# Copia el jar compilado al directorio raíz y le da un nombre fijo
RUN cp target/api-usuario-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Ejecuta el jar
CMD ["java", "-jar", "app.jar"]
