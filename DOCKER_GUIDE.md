# Workspace Docker Overview

워크스페이스 내 두 프로젝트의 Docker 환경 구성

## 🚀 프로젝트별 포트 할당

### Campstation

| 서비스     | 포트       | 비고              |
| ---------- | ---------- | ----------------- |
| Frontend   | 3000       | Vue.js            |
| Backend    | 8080       | Spring Boot       |
| PostgreSQL | 5432       | 데이터베이스      |
| Redis      | 6379       | 캐시              |
| MinIO      | 9000, 9001 | 오브젝트 스토리지 |
| MailHog    | 1025, 8025 | 메일 서버         |

### PSMO Community

| 서비스     | 포트       | 비고              |
| ---------- | ---------- | ----------------- |
| Frontend   | 3001       | Vue.js            |
| Backend    | 8081       | Ktor              |
| PostgreSQL | 5433       | 데이터베이스      |
| Redis      | 6380       | 캐시              |
| MinIO      | 9002, 9003 | 오브젝트 스토리지 |
| MailHog    | 1026, 8026 | 메일 서버         |

## 🎯 빠른 시작

### Campstation 실행

```bash
cd campstation
docker compose up -d
```

### PSMO Community 실행

```bash
cd psmo-community
docker compose up -d
```

### 두 프로젝트 동시 실행

```bash
# Terminal 1
cd campstation && docker compose up

# Terminal 2
cd psmo-community && docker compose up
```

## 📋 공통 기능

### 개발 환경

- ✅ Hot Reload (코드 변경 자동 반영)
- ✅ Docker Compose Watch
- ✅ 볼륨 마운트로 실시간 개발

### 운영 환경

- ✅ 다중 단계 빌드 (최소 이미지)
- ✅ 헬스체크 자동화
- ✅ 환경변수 기반 설정
- ✅ 비 root 사용자 실행

## 🛡️ 보안 설정

각 프로젝트의 `.env.example`을 복사하여 `.env` 파일 생성:

```bash
# Campstation
cd campstation
cp .env.example .env

# PSMO Community
cd psmo-community
cp .env.example .env
```

⚠️ **운영 환경에서는 반드시 비밀번호를 변경하세요!**

## 🔧 전체 정리

모든 Docker 리소스 정리:

```bash
# 모든 컨테이너 중지 및 삭제
docker compose -f campstation/docker-compose.yml down -v
docker compose -f psmo-community/docker-compose.yml down -v

# 사용하지 않는 이미지 정리
docker system prune -a
```

## 📚 상세 문서

- [Campstation Docker 가이드](./campstation/DOCKER.md)
- [PSMO Community Docker 가이드](./psmo-community/DOCKER.md)
