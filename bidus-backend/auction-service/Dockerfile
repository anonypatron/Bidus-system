FROM eclipse-temurin:21-jdk-jammy AS builder

# 루트 디렉터리에서 실행 -> docker build -t auction-service -f auction-service/Dockerfile .
WORKDIR /app

COPY . .

RUN ./gradlew :auction-service:bootJar

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/auction-service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]