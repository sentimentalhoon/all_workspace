# PSMO Community

커뮤니티 기반 플랫폼

## 프로젝트 구조

```
psmo-community/
├── backend/          # Ktor 3.0.2 + Java 21
└── frontend/         # Vue.js 3
```

## 기술 스택

### Backend

- **Java**: 21
- **Kotlin**: 2.1.0
- **Ktor**: 3.0.2
- **Exposed**: ORM
- **Database**: H2 (개발), MySQL (운영)
- **Build Tool**: Gradle

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
./gradlew run
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

## API 엔드포인트

- Health Check: `GET /api/health`

## 주요 기능 (예정)

- 사용자 인증 및 권한 관리
- 커뮤니티 게시판
- 프로필 관리
- 실시간 알림
- 검색 및 필터링

## 라이선스

Proprietary
