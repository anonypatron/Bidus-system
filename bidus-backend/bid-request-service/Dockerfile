FROM eclipse-temurin:21-jdk-jammy AS builder

# 루트 디렉터리에서 실행 -> docker build -t analysis-service -f analysis-service/Dockerfile .
WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew :bid-request-service:bootJar

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/bid-request-service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]