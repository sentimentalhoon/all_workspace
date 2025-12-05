# All Workspace

커뮤니티 플랫폼(PSMO Community)을 포함한 워크스페이스입니다.

## 📁 프로젝트 구조

```
.
├── psmo-community/          # 커뮤니티 플랫폼
│   ├── backend/             # Ktor 백엔드
│   └── frontend/            # Vue.js 프론트엔드
├── infrastructure/          # 인프라 설정
│   └── nginx/              # Nginx 리버스 프록시
└── docs/                   # 문서
```

## 🚀 빠른 시작

### 개발 환경

1. **환경 변수 설정**

   ```bash
   cp .env.dev.example .env.dev
   ```

2. **Docker Compose로 실행**

   ```bash
   docker-compose -f docker-compose.dev.yml up -d
   ```

3. **접속**
   - PSMO Frontend: http://localhost:5174
   - PSMO Backend: http://localhost:8081
   - PSMO MailHog: http://localhost:8026
   - PSMO MinIO Console: http://localhost:9003

### 프로덕션 환경

1. **환경 변수 설정**

   ```bash
   cp .env.prod.example .env.prod
   # .env.prod 파일을 열어 실제 값으로 수정
   ```

2. **SSL 인증서 설정**

   - [SSL_SETUP.md](./SSL_SETUP.md) 참조

3. **Docker Compose로 실행**

   ```bash
   docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d
   ```

4. **접속**
   - PSMO Community: https://psmo.your-domain.com

## 🛠️ 개발 환경 상세

### 포트 매핑 (개발)

| 서비스        | 포트       | 설명           |
| ------------- | ---------- | -------------- |
| PSMO Frontend | 5174       | Vue 개발 서버  |
| PSMO Backend  | 8081       | Ktor           |
| PSMO Postgres | 5433       | PostgreSQL     |
| PSMO Redis    | 6380       | Redis          |
| PSMO MinIO    | 9002, 9003 | MinIO 스토리지 |
| PSMO MailHog  | 1026, 8026 | 이메일 테스트  |

### 개발 환경 특징

- **핫 리로딩**: 소스 코드 변경 시 자동 반영 (볼륨 마운트)
- **디버깅**: 백엔드 디버깅 포트 개방 (5006)
- **데이터베이스**: 로컬 PostgreSQL 컨테이너
- **이메일**: MailHog로 이메일 테스트
- **스토리지**: MinIO로 파일 업로드 테스트

## 🔧 개발 명령어

### 개발 환경 시작

```bash
# 전체 서비스 시작
docker-compose -f docker-compose.dev.yml up -d

# 특정 프로젝트만 시작
docker-compose -f docker-compose.dev.yml up -d psmo-backend psmo-frontend psmo-postgres psmo-redis

# 로그 확인
docker-compose -f docker-compose.dev.yml logs -f
```

### 개발 환경 중지

```bash
# 전체 중지
docker-compose -f docker-compose.dev.yml down

# 볼륨까지 삭제 (데이터 초기화)
docker-compose -f docker-compose.dev.yml down -v
```

### 빌드 재실행

```bash
# 이미지 재빌드
docker-compose -f docker-compose.dev.yml build --no-cache

# 재빌드 후 시작
docker-compose -f docker-compose.dev.yml up -d --build
```

## 📚 기술 스택

### PSMO Community

- **Frontend**: Vue.js 3, Vite, Pinia, Vue Router
- **Backend**: Ktor 3.0.2, Exposed ORM, JWT
- **Database**: PostgreSQL 17
- **Cache**: Redis 7
- **Storage**: MinIO

### Infrastructure

- **Reverse Proxy**: Nginx
- **SSL**: Let's Encrypt (Certbot)
- **Containerization**: Docker, Docker Compose

## 📖 문서

- [배포 가이드](./DEPLOYMENT_GUIDE.md)
- [Docker 가이드](./DOCKER_GUIDE.md)
- [SSL 설정 가이드](./SSL_SETUP.md)

## 🔐 보안

- JWT 기반 인증
- BCrypt 패스워드 암호화
- CORS 설정
- HTTPS (프로덕션)
- 환경 변수로 민감 정보 관리

## 🤝 기여

이 프로젝트는 개인 학습 프로젝트입니다.

## 📝 라이선스

MIT License
