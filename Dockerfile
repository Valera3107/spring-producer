FROM openjdk:latest

ADD target/demo-0.0.1-SNAPSHOT.jar app-producer.jar

ENTRYPOINT ["java", "-jar", "/app-producer.jar"]

EXPOSE 8000
