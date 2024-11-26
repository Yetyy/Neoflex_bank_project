FROM openjdk:21-jdk-alpine
VOLUME /tmp
COPY target/deal-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
