# syntax=docker/dockerfile:1

# 1) 빌드 스테이지 — Gradle 래퍼로 bootJar 생성 (DB/Kafka 필요한 통합 테스트는 제외)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon -x test

# 2) 런타임 스테이지 — JRE만 포함
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
