FROM eclipse-temurin:21-jdk-jammy AS builder

# 루트 디렉터리에서 실행 -> docker build -t notification-service -f notification-service/Dockerfile .
WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew :notification-service:bootJar

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/notification-service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]