FROM openjdk:23-jdk-apline
ARG JAR_FILE=target/*.jar
COPY ./target/inventory-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]