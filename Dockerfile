FROM maven:3.8.6-openjdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:11-jre-alpine 
WORKDIR /app
COPY --from=build /app/target/*jar-with-dependencies.jar app.jar
ENTRYPOINT ["echo", "Build and tests completed successfully!"]