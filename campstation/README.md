# Campstation

캠핑장 예약 및 관리 시스템

## 프로젝트 구조

```
campstation/
├── backend/          # Spring Boot 4.0.0 + Java 21
└── frontend/         # Vue.js 3
```

## 기술 스택

### Backend

- **Java**: 21
- **Spring Boot**: 4.0.0
- **Spring Data JPA**
- **Database**: H2 (개발), MySQL (운영)
- **Build Tool**: Maven

### Frontend

- **Vue.js**: 3.5+
- **Vue Router**: 4
- **Pinia**: 상태 관리
- **Vite**: 6
- **Axios**: HTTP 클라이언트

## 시작하기

### Backend 실행

```bash
cd backend
./mvnw spring-boot:run
```

백엔드는 `http://localhost:8080`에서 실행됩니다.

### Frontend 실행

```bash
cd frontend
npm install
npm run dev
```

프론트엔드는 `http://localhost:3000`에서 실행됩니다.

## 개발 환경

- **Backend Port**: 8080
- **Frontend Port**: 3000
- **H2 Console**: http://localhost:8080/h2-console (개발 환경)

## API 엔드포인트

- Health Check: `GET /api/health`

## 주요 기능 (예정)

- 캠핑장 검색 및 조회
- 예약 관리
- 사용자 인증 및 권한 관리
- 리뷰 및 평점 시스템

## 라이선스

Proprietary
