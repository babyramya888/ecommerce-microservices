FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
RUN mvn -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/quarkus-app/lib/ /app/lib/
COPY --from=build /workspace/target/quarkus-app/*.jar /app/
COPY --from=build /workspace/target/quarkus-app/app/ /app/app/
COPY --from=build /workspace/target/quarkus-app/quarkus/ /app/quarkus/
COPY src/main/resources/privateKey.pem /app/privateKey.pem
COPY src/main/resources/publicKey.pem /app/publicKey.pem
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
