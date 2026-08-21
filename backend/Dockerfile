FROM eclipse-temurin:25-jdk-alpine AS dependencies
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline

FROM dependencies AS build
COPY src src
RUN ./mvnw -DskipTests package

FROM dependencies AS development
COPY src src
CMD ["./mvnw", "spring-boot:run"]

FROM build AS test
RUN ./mvnw test

FROM eclipse-temurin:25-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S app && adduser -S -G app app
COPY --from=build --chown=app:app /app/target/demo-0.0.1-SNAPSHOT.jar app.jar
USER app
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=5 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"'
ENTRYPOINT ["java", "-jar", "app.jar"]
