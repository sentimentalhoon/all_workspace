# All Workspace - 프로젝트 개요

## 📋 프로젝트 구성

이 워크스페이스는 단일 Ubuntu 서버에서 Docker Compose를 통해 PSMO Community 프로젝트를 운영합니다.

### PSMO Community (커뮤니티 플랫폼)

- **도메인**: https://mycommunity.duckdns.org
- **백엔드**: Ktor 3.0.2 + Kotlin 2.1.0 + Java 21
- **프론트엔드**: Vue.js 3.5.13 + Vite 6.0.3

## 🏗️ 인프라 아키텍처

```
┌─────────────────────────────────────────────────────────┐
│                    인터넷 (외부)                          │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
            ┌──────────────────┐
            │   Ubuntu Server   │
            │   (59.3.21.233)   │
            └────────┬──────────┘
                     │
        ┌────────────┴────────────┐
        ▼                         ▼
   Port 80/443              Port 80/443
        │                         │
┌───────┴──────────────────────────┴──────┐
│         Nginx Reverse Proxy             │
│    (nginx-proxy 컨테이너)                │
│                                          │
│  mycommunity.duckdns.org → PSMO         │
└────┬─────────────────────────────┬──────┘
     │                             │
     ▼                             ▼
┌──────────────┐            ┌──────────────┐
│              │            │     PSMO     │
│              │            │   Services   │
├──────────────┤            ├──────────────┤
│              │            │ Frontend:80  │
│              │            │ Backend:8080 │
│              │            │ Postgres:5433│
│              │            │ Redis:6380   │
│              │            │ MinIO:9002   │
│              │            │ MailHog:1026 │
└──────────────┘            └──────────────┘
```

## 🛠️ 기술 스택

### 공통

- **컨테이너화**: Docker, Docker Compose
- **웹 서버**: Nginx 1.27-alpine
- **SSL**: Let's Encrypt (Certbot)
- **DNS**: DuckDNS

### PSMO Community

| 구분            | 기술       | 버전      |
| --------------- | ---------- | --------- |
| 백엔드          | Ktor       | 3.0.2     |
| 언어            | Kotlin     | 2.1.0     |
| JVM             | Java       | 21        |
| 빌드 도구       | Gradle     | 8.11.1    |
| 프론트엔드      | Vue.js     | 3.5.13    |
| 번들러          | Vite       | 6.0.3     |
| 상태 관리       | Pinia      | 2.3.0     |
| 라우터          | Vue Router | 4.5.0     |
| HTTP 클라이언트 | Axios      | 1.7.9     |
| 데이터베이스    | PostgreSQL | 17-alpine |
| 캐시            | Redis      | 7-alpine  |
| 객체 스토리지   | MinIO      | latest    |
| 메일 테스트     | MailHog    | latest    |

## 📂 프로젝트 구조

```
all_workspace/
├── psmo-community/
│   ├── backend/              # Ktor 백엔드
│   │   ├── src/
│   │   ├── build.gradle.kts
│   │   ├── Dockerfile
│   │   └── Dockerfile.dev
│   └── frontend/             # Vue.js 프론트엔드
│       ├── src/
│       ├── package.json
│       ├── Dockerfile
│       └── nginx.conf
│
├── infrastructure/
│   └── nginx/
│       ├── nginx.conf
│       ├── conf.d/
│       │   └── psmo-community.conf
│       └── Dockerfile
│
├── docs/                     # 프로젝트 문서
│
├── docker-compose.yml        # 개발 환경
├── docker-compose.prod.yml   # 프로덕션 환경
└── .env.example
```

## 🌐 포트 할당

### PSMO Community

- Frontend: 3001 (dev), 80 (container)
- Backend: 8081 (dev), 8080 (container)
- PostgreSQL: 5433 (container)
- Redis: 6380 (container)
- MinIO: 9002 (container), 9003 (console)
- MailHog: 1026 (SMTP), 8026 (Web UI)

### Infrastructure

- Nginx: 80 (HTTP), 443 (HTTPS)

## 🔐 SSL 인증서

- **발급 기관**: Let's Encrypt
- **도구**: Certbot
- **갱신 주기**: 90일 (자동 갱신 필요)
- **저장 위치**: `/etc/letsencrypt/live/`
  - mycommunity.duckdns.org

## 🚀 배포 가이드

### 전체 시스템 시작

```bash
cd ~/all_workspace
sudo docker compose -f docker-compose.prod.yml up -d
```

### 특정 서비스 재시작

```bash
# PSMO 프론트엔드만
sudo docker compose -f docker-compose.prod.yml restart psmo-frontend
```

### 코드 변경 후 재배포

```bash
# 1. 최신 코드 가져오기
git pull

# 2. 재빌드 및 재시작
sudo docker compose -f docker-compose.prod.yml build [service-name]
sudo docker compose -f docker-compose.prod.yml up -d [service-name]
```

### 전체 시스템 중지

```bash
sudo docker compose -f docker-compose.prod.yml down
```

## 🔍 모니터링 명령어

### 컨테이너 상태 확인

```bash
docker ps
docker ps -a  # 중지된 컨테이너 포함
```

### 로그 확인

```bash
# 실시간 로그
docker logs -f [container-name]

# 최근 100줄
docker logs --tail 100 [container-name]

# Nginx 에러 로그
docker exec nginx-proxy cat /var/log/nginx/psmo_error.log
```

### 헬스체크

```bash
# API 헬스체크
curl https://mycommunity.duckdns.org/api/health

# 컨테이너 내부 헬스체크
docker exec psmo-backend-prod wget -qO- http://localhost:8080/api/health
```

## 🗄️ 데이터베이스 접속

### PSMO PostgreSQL

```bash
docker exec -it psmo-postgres-prod psql -U psmo -d psmo_community
```

### Redis

```bash
# PSMO
docker exec -it psmo-redis-prod redis-cli -a [REDIS_PASSWORD]
```

## 🔧 환경 변수

프로덕션 배포 시 `.env` 파일 필요:

```bash
# PSMO
PSMO_POSTGRES_PASSWORD=
PSMO_REDIS_PASSWORD=
PSMO_MINIO_USER=
PSMO_MINIO_PASSWORD=
```

## 📝 주요 엔드포인트

### PSMO Community

- 메인: https://mycommunity.duckdns.org
- API: https://mycommunity.duckdns.org/api/*
- 헬스체크: https://mycommunity.duckdns.org/api/health

## 🐛 트러블슈팅

### 컨테이너가 unhealthy 상태

1. 로그 확인: `docker logs [container-name]`
2. 헬스체크 직접 테스트: `docker exec [container] wget -qO- http://localhost:8080/api/health`
3. 재시작: `sudo docker compose -f docker-compose.prod.yml restart [service]`

### SSL 인증서 갱신

```bash
sudo certbot renew
sudo docker compose -f docker-compose.prod.yml restart nginx
```

### NAT Loopback 문제 (같은 공유기 내부에서 접속 안됨)

Windows에서 DNS 캐시 초기화:

```powershell
ipconfig /flushdns
```

또는 `C:\Windows\System32\drivers\etc\hosts`에 서버 내부 IP 추가

## 📚 추가 문서

- [배포 가이드](./DEPLOYMENT.md) - 상세 배포 절차
- [개발 가이드](./DEVELOPMENT.md) - 로컬 개발 환경 설정
- [API 문서](./API.md) - API 엔드포인트 명세
- [트러블슈팅](./TROUBLESHOOTING.md) - 일반적인 문제 해결

## 👥 팀 & 연락처

- **Repository**: https://github.com/sentimentalhoon/all_workspace
- **Server**: sentimentalhoon@mycamp (Ubuntu)

## 📅 프로젝트 타임라인

- **2025-12-01**: 초기 배포 완료
  - 두 프로젝트 Docker 컨테이너화
  - Nginx 리버스 프록시 설정
  - SSL 인증서 발급 및 적용
  - 프로덕션 환경 구축 완료
