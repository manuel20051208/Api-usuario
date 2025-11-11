FROM eclipse-temurin:23-jdk

WORKDIR /app

COPY target/api-usuario-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
