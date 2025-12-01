# Campstation - Docker Guide

## 🐳 Docker 실행 방법

### 개발 환경

```bash
# 개발 환경 실행 (watch 모드)
docker compose up --build

# 백그라운드 실행
docker compose up -d

# 로그 확인
docker compose logs -f

# 중지
docker compose down

# 볼륨까지 삭제
docker compose down -v
```

### 운영 환경

```bash
# .env 파일 생성 (먼저 .env.example을 복사)
cp .env.example .env
# .env 파일의 비밀번호들을 변경하세요!

# 운영 환경 실행
docker compose -f docker-compose.prod.yml up --build -d

# 중지
docker compose -f docker-compose.prod.yml down
```

## 📡 포트 할당

| 서비스        | 개발 포트 | 운영 포트 | 용도              |
| ------------- | --------- | --------- | ----------------- |
| Frontend      | 3000      | 3000      | 웹 애플리케이션   |
| Backend       | 8080      | 8080      | API 서버          |
| PostgreSQL    | 5432      | 5432      | 데이터베이스      |
| Redis         | 6379      | 6379      | 캐시              |
| MinIO         | 9000      | 9000      | 오브젝트 스토리지 |
| MinIO Console | 9001      | 9001      | MinIO 관리 콘솔   |
| MailHog SMTP  | 1025      | 1025      | 메일 서버         |
| MailHog UI    | 8025      | 8025      | 메일 확인 UI      |

## 🔧 개별 서비스 접속

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api/health
- **MinIO Console**: http://localhost:9001 (campstation / campstation_minio_dev)
- **MailHog**: http://localhost:8025
- **PostgreSQL**: localhost:5432 (campstation / campstation_dev_password)

## 🔄 개발 중 변경사항 자동 반영

Docker Compose watch 기능을 사용하여 코드 변경시 자동으로 반영됩니다:

- Frontend: 소스 변경 → 즉시 반영
- Backend: 소스 변경 → 자동 재시작

## 📦 주요 기능

### 다중 단계 빌드

- 운영 이미지 최소화
- 빌드 의존성 분리

### 헬스체크

- 자동 상태 확인
- 의존성 순서 보장

### 볼륨 관리

- 데이터 영속성
- 캐시 최적화

### 네트워크 격리

- 서비스별 독립 네트워크
- 보안 강화

## 🛠️ 유용한 명령어

```bash
# 특정 서비스만 재시작
docker compose restart backend

# 특정 서비스 로그만 확인
docker compose logs -f backend

# 실행중인 컨테이너 확인
docker compose ps

# 리소스 사용량 확인
docker stats

# 볼륨 확인
docker volume ls

# 사용하지 않는 리소스 정리
docker system prune -a
```

## ⚠️ 주의사항

1. **운영 환경**: 반드시 `.env` 파일의 비밀번호를 변경하세요
2. **포트 충돌**: 다른 서비스가 같은 포트를 사용중이면 변경 필요
3. **리소스**: 최소 4GB 이상의 RAM 권장
