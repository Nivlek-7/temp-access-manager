FROM eclipse-temurin:25-jdk-alpine AS base
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline
COPY src ./src

FROM base AS test
RUN ./mvnw test

FROM base AS development
CMD ["./mvnw", "spring-boot:run"]
