FROM maven:3.9.5-eclipse-temurin-21 AS builder

WORKDIR /app

COPY ../pom.xml .
COPY ../common-lib ./common-dto
COPY . .

RUN mvn clean install -DskipTests

FROM openjdk:21-jdk

WORKDIR /app

COPY --from=builder /app/statement/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
