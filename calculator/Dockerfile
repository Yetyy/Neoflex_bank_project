
FROM openjdk:21-jdk



WORKDIR /app


COPY . .


RUN ./mvnw clean package


CMD ["java", "-jar", "target/calculator-0.0.1-SNAPSHOT.jar"]
