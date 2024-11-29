FROM openjdk:21-jdk
VOLUME /tmp
COPY target/deal-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar","/app.jar"]

