FROM eclipse-temurin:23-jdk

WORKDIR /app
COPY . .

# 🔥 Solución: dar permisos de ejecución al wrapper
RUN chmod +x mvnw

RUN ./mvnw -B clean package -DskipTests

EXPOSE 8080
CMD ["java", "-jar", "target/*.jar"]
