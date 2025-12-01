# 통합 배포 가이드

## 🎯 개요

하나의 서버에서 Campstation과 PSMO Community를 동시에 실행합니다.

## 🏗️ 아키텍처

```
인터넷
  │
  ├── https://mycamp.duckdns.org (443)
  │   └── Nginx Proxy → Campstation Frontend (80)
  │                   └── Campstation Backend (8080)
  │
  └── https://mycommunity.duckdns.org (443)
      └── Nginx Proxy → PSMO Frontend (80)
                      └── PSMO Backend (8080)
```

## 📦 서비스 구성

### Campstation
- Frontend (Vue.js + Nginx)
- Backend (Spring Boot)
- PostgreSQL
- Redis
- MinIO
- MailHog

### PSMO Community
- Frontend (Vue.js + Nginx)
- Backend (Ktor)
- PostgreSQL
- Redis
- MinIO
- MailHog

### Nginx Reverse Proxy
- SSL/TLS 종료
- 도메인 기반 라우팅
- Rate Limiting
- 보안 헤더

## 🚀 배포 단계

### 1. SSL 인증서 설치

```bash
# 디렉토리 생성
mkdir -p infrastructure/nginx/ssl/campstation
mkdir -p infrastructure/nginx/ssl/psmo

# 인증서 복사 (실제 경로로 변경)
cp /your/cert/path/campstation/* infrastructure/nginx/ssl/campstation/
cp /your/cert/path/psmo/* infrastructure/nginx/ssl/psmo/
```

상세 내용: [SSL_SETUP.md](./SSL_SETUP.md)

### 2. 환경 변수 설정

```bash
# .env 파일 생성
cp .env.prod.example .env

# 모든 비밀번호를 강력한 값으로 변경
nano .env
```

⚠️ **중요**: 각 비밀번호를 고유하고 강력한 값으로 변경하세요!

### 3. Docker Compose 실행

```bash
# 전체 스택 실행
docker compose -f docker-compose.prod.yml up --build -d

# 진행 상황 확인
docker compose -f docker-compose.prod.yml ps
docker compose -f docker-compose.prod.yml logs -f
```

### 4. 접속 확인

- **Campstation**: https://mycamp.duckdns.org
- **PSMO Community**: https://mycommunity.duckdns.org

## 🔍 모니터링

```bash
# 모든 서비스 상태
docker compose -f docker-compose.prod.yml ps

# 특정 서비스 로그
docker compose -f docker-compose.prod.yml logs -f nginx
docker compose -f docker-compose.prod.yml logs -f campstation-backend
docker compose -f docker-compose.prod.yml logs -f psmo-backend

# 리소스 사용량
docker stats
```

## 🛠️ 유지보수

### 서비스 재시작

```bash
# Nginx만 재시작
docker compose -f docker-compose.prod.yml restart nginx

# 특정 프로젝트 재시작
docker compose -f docker-compose.prod.yml restart campstation-backend campstation-frontend
docker compose -f docker-compose.prod.yml restart psmo-backend psmo-frontend
```

### 업데이트 배포

```bash
# 코드 변경 후
git pull

# 특정 서비스만 재빌드
docker compose -f docker-compose.prod.yml up --build -d campstation-backend

# 또는 전체 재빌드
docker compose -f docker-compose.prod.yml up --build -d
```

### 백업

```bash
# 데이터베이스 백업
docker compose -f docker-compose.prod.yml exec campstation-postgres pg_dump -U campstation campstation > backup_campstation_$(date +%Y%m%d).sql
docker compose -f docker-compose.prod.yml exec psmo-postgres pg_dump -U psmo psmo_community > backup_psmo_$(date +%Y%m%d).sql

# 볼륨 백업
docker run --rm -v campstation_postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/campstation_db_backup.tar.gz /data
```

## 🔒 보안 체크리스트

- [x] SSL/TLS 인증서 설치
- [ ] .env 파일의 모든 비밀번호 변경
- [x] Rate Limiting 활성화
- [x] 보안 헤더 설정
- [ ] 방화벽 설정 (80, 443 포트만 열기)
- [ ] 정기적인 보안 업데이트

## 📊 시스템 요구사항

### 최소 사양
- CPU: 4 cores
- RAM: 8 GB
- Disk: 50 GB SSD
- Network: 100 Mbps

### 권장 사양
- CPU: 8 cores
- RAM: 16 GB
- Disk: 100 GB SSD
- Network: 1 Gbps

## 🆘 문제 해결

### 1. Nginx 접속 불가
```bash
# Nginx 설정 테스트
docker compose -f docker-compose.prod.yml exec nginx nginx -t

# 로그 확인
docker compose -f docker-compose.prod.yml logs nginx
```

### 2. SSL 인증서 오류
```bash
# 인증서 파일 확인
ls -la infrastructure/nginx/ssl/campstation/
ls -la infrastructure/nginx/ssl/psmo/

# 권한 확인
chmod 644 infrastructure/nginx/ssl/*/fullchain.pem
chmod 600 infrastructure/nginx/ssl/*/privkey.pem
```

### 3. 백엔드 연결 실패
```bash
# 헬스체크 확인
curl http://localhost:8080/api/health  # Campstation
curl http://localhost:8081/api/health  # PSMO (내부 포트)

# 컨테이너 네트워크 확인
docker network inspect docker-compose-prod_proxy-network
```

### 4. 데이터베이스 연결 오류
```bash
# PostgreSQL 상태 확인
docker compose -f docker-compose.prod.yml exec campstation-postgres pg_isready -U campstation
docker compose -f docker-compose.prod.yml exec psmo-postgres pg_isready -U psmo
```

## 🔄 롤백

문제 발생시 이전 버전으로 롤백:

```bash
# 서비스 중지
docker compose -f docker-compose.prod.yml down

# 이전 커밋으로 복원
git checkout <previous-commit>

# 재배포
docker compose -f docker-compose.prod.yml up --build -d
```

## 📞 지원

문제가 계속되면 로그를 확인하고:
```bash
docker compose -f docker-compose.prod.yml logs > debug_logs.txt
```
