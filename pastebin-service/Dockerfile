FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY pastebin-common/pom.xml ./pastebin-common/
COPY pastebin-service/pom.xml ./pastebin-service/
COPY hash-generator-service/pom.xml ./hash-generator-service/
COPY pastebin-gateway/pom.xml ./pastebin-gateway/

COPY pastebin-common/src ./pastebin-common/src
COPY pastebin-service/src ./pastebin-service/src
COPY hash-generator-service/src ./hash-generator-service/src
COPY pastebin-gateway/src ./pastebin-gateway/src

RUN mvn clean package -pl pastebin-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/pastebin-service/target/pastebin-service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]