FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Maven wrapper 복사
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# 의존성 다운로드
RUN ./mvnw dependency:go-offline

# 소스 코드 복사 및 빌드
COPY src ./src
RUN ./mvnw clean package -DskipTests

# 런타임 이미지
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /app/target/*.jar app.jar

# 비 root 사용자 생성
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 헬스체크
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
