ARG PROJECT

FROM maven:3.9.9-eclipse-temurin-17 AS build

ARG PROJECT
WORKDIR /build
COPY ${PROJECT}/pom.xml ./pom.xml
COPY ${PROJECT}/src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
COPY --from=build /build/target/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
