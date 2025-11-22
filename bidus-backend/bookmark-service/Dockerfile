FROM eclipse-temurin:21-jdk-jammy AS builder

# 루트 디렉터리에서 실행 -> docker build -t bookmark-service -f bookmark-service/Dockerfile .
WORKDIR /app

COPY . .

RUN ./gradlew :bookmark-service:bootJar

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/bookmark-service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]