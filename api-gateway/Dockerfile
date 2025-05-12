FROM gradle:8.12.1-jdk17 as build
WORKDIR /app
COPY . /app/
RUN gradle build -x test

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]