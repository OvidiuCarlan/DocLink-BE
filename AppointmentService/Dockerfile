FROM gradle:8.12.1-jdk17 as build
WORKDIR /app
COPY . /app/
RUN gradle build -x test

# Use latest jammy image and explicitly update dpkg
FROM eclipse-temurin:17-jre-jammy
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y --only-upgrade dpkg && \
    dpkg --version && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]