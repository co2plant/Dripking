FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

COPY gradlew gradlew.bat build.gradle settings.gradle ./
COPY gradle ./gradle
RUN chmod +x ./gradlew

COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S dripking \
    && adduser -S dripking -G dripking \
    && mkdir -p /var/log/dripking \
    && chown -R dripking:dripking /var/log/dripking

COPY --from=build /workspace/build/libs/*.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

USER dripking

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
