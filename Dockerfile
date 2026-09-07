FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "target/congresso-0.0.1-SNAPSHOT.jar"]