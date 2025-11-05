FROM eclipse-temurin:21-jdk-jammy AS builder

# 루트 디렉터리에서 실행 -> docker build -t my-auth-service -f auth-service/Dockerfile .
WORKDIR /app

COPY . .

RUN ./gradlew :auth-service:bootJar

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/auth-service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]