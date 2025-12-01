# Campstation Backend

Spring Boot 4.0.0 + Java 21 기반 백엔드 애플리케이션

## 기술 스택

- Java 21
- Spring Boot 4.0.0
- Spring Data JPA
- H2 Database (개발)
- MySQL (운영)
- Lombok
- Maven

## 실행 방법

### 개발 환경

```bash
./mvnw spring-boot:run
```

### 운영 환경

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## API 엔드포인트

- Health Check: `GET /api/health`
- H2 Console: `http://localhost:8080/h2-console` (개발 환경)

## 포트

- 8080 (기본 포트)
