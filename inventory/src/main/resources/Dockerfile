FROM openjdk:23-jdk-apline
ARG JAR_FILE=target/*.jar
COPY ./target/NAMEMEMEME.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]