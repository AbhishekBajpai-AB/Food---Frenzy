FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn --batch-mode --no-transfer-progress verify

FROM eclipse-temurin:17-jre
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=prod
COPY --from=build /workspace/target/FoodFrenzy-0.0.1-SNAPSHOT.jar /app/foodfrenzy.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/foodfrenzy.jar"]
