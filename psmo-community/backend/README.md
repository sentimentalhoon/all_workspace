# PSMO Community Backend

Ktor 3.0.2 + Java 21 기반 백엔드 애플리케이션

## 기술 스택

- Java 21
- Kotlin 2.1.0
- Ktor 3.0.2
- Exposed (ORM)
- H2 Database (개발)
- MySQL (운영)
- Gradle

## 실행 방법

### 개발 환경

```bash
./gradlew run
```

### 빌드

```bash
./gradlew build
```

### 테스트

```bash
./gradlew test
```

## API 엔드포인트

- Health Check: `GET /api/health`

## 포트

- 8080 (기본 포트)

## 주요 기능

- RESTful API
- JSON 직렬화
- CORS 설정
- 로깅
- 상태 페이지
