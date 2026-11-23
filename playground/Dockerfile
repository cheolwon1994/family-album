FROM gradle:9.4.1-jdk21 AS build
WORKDIR /workspace
COPY build.gradle.kts settings.gradle.kts .
COPY src ./src
RUN gradle --no-daemon bootJar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/spring-server-0.0.1-SNAPSHOT.jar /app/app.jar
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70.0"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java -jar /app/app.jar"]
