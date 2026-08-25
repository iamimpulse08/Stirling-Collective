FROM openjdk:25-jdk-apline
WORKDIR /app
COPY ./target/inventory-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]