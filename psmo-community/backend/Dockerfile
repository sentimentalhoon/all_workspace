# Multi-stage build for PSMO Backend

# Build stage
FROM gradle:8.5-jdk21-alpine AS build
WORKDIR /app

# Copy Gradle files
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY gradlew ./

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src ./src

# Build application
RUN ./gradlew buildFatJar --no-daemon

# Production stage
FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app

# Create non-root user
RUN addgroup -S ktor && adduser -S ktor -G ktor

# Copy JAR from build stage
COPY --from=build /app/build/libs/*-all.jar app.jar

# Change ownership
RUN chown -R ktor:ktor /app

USER ktor

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
