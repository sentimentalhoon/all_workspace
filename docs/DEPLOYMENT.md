# 배포 가이드

## 📋 배포 전 준비사항

### 1. 서버 요구사항

- **OS**: Ubuntu 20.04 LTS 이상
- **Docker**: 최신 버전
- **Docker Compose**: 최신 버전
- **메모리**: 최소 4GB RAM
- **디스크**: 최소 20GB 여유 공간

### 2. 도메인 설정

- DuckDNS 계정 생성
- 도메인 등록:
  - mycommunity.duckdns.org → 서버 공인 IP

### 3. 방화벽 설정

```bash
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw allow 22/tcp    # SSH
sudo ufw enable
```

### 4. 포트 포워딩

라우터 설정에서:

- 80 → Ubuntu 서버 IP:80
- 443 → Ubuntu 서버 IP:443

## 🚀 초기 배포

### 1. 서버 접속

```bash
ssh sentimentalhoon@mycamp
```

### 2. 저장소 클론

```bash
cd ~
git clone https://github.com/sentimentalhoon/all_workspace.git
cd all_workspace
```

### 3. 환경 변수 설정

```bash
cp .env.prod.example .env
nano .env
```

필수 환경 변수:

```env
# PSMO
PSMO_POSTGRES_PASSWORD=강력한비밀번호
PSMO_REDIS_PASSWORD=강력한비밀번호
PSMO_MINIO_USER=admin
PSMO_MINIO_PASSWORD=강력한비밀번호
```

### 4. SSL 인증서 발급

#### Nginx 임시 중지

```bash
sudo docker compose -f docker-compose.prod.yml stop nginx 2>/dev/null || true
```

#### Certbot 설치

```bash
sudo apt update
sudo apt install certbot -y
```

#### 인증서 발급

```bash
# PSMO Community
sudo certbot certonly --standalone -d mycommunity.duckdns.org
```

#### 인증서 확인

```bash
sudo ls -la /etc/letsencrypt/live/mycommunity.duckdns.org/
```

### 5. Docker 이미지 빌드 및 실행

```bash
# 전체 빌드 (최초 실행 시)
sudo docker compose -f docker-compose.prod.yml build

# 컨테이너 시작
sudo docker compose -f docker-compose.prod.yml up -d
```

### 6. 배포 확인

```bash
# 컨테이너 상태 확인
docker ps

# 헬스체크
curl https://mycommunity.duckdns.org/api/health

# 로그 확인
docker logs psmo-backend-prod
docker logs nginx-proxy
```

## 🔄 업데이트 배포

### 코드 변경 후 재배포

#### 1. 최신 코드 가져오기

```bash
cd ~/all_workspace
git pull
```

#### 2. 특정 서비스 재배포

**PSMO 백엔드**

```bash
sudo docker compose -f docker-compose.prod.yml build psmo-backend
sudo docker compose -f docker-compose.prod.yml up -d psmo-backend
```

**PSMO 프론트엔드**

```bash
sudo docker compose -f docker-compose.prod.yml build psmo-frontend
sudo docker compose -f docker-compose.prod.yml up -d psmo-frontend
```

**Nginx**

```bash
sudo docker compose -f docker-compose.prod.yml build nginx
sudo docker compose -f docker-compose.prod.yml up -d nginx
```

#### 3. 전체 재배포 (권장하지 않음)

```bash
sudo docker compose -f docker-compose.prod.yml down
sudo docker compose -f docker-compose.prod.yml up -d --build
```

### 무중단 배포 (Rolling Update)

```bash
# 1. 새 이미지 빌드
sudo docker compose -f docker-compose.prod.yml build [service-name]

# 2. 기존 컨테이너 중지 없이 새 컨테이너 시작
sudo docker compose -f docker-compose.prod.yml up -d --no-deps [service-name]
```

## 🔐 SSL 인증서 갱신

### 자동 갱신 설정

#### Cron 작업 추가

```bash
sudo crontab -e
```

다음 라인 추가 (매월 1일 오전 3시 갱신 시도):

```cron
0 3 1 * * certbot renew --quiet && docker compose -f /home/sentimentalhoon/all_workspace/docker-compose.prod.yml restart nginx
```

### 수동 갱신

```bash
# Nginx 중지
sudo docker compose -f docker-compose.prod.yml stop nginx

# 인증서 갱신
sudo certbot renew

# Nginx 재시작
sudo docker compose -f docker-compose.prod.yml up -d nginx
```

## 📊 모니터링

### 실시간 로그 확인

```bash
# 모든 서비스
sudo docker compose -f docker-compose.prod.yml logs -f

# 특정 서비스
docker logs -f psmo-backend-prod
docker logs -f nginx-proxy
```

### 리소스 사용량 확인

```bash
# 전체 컨테이너
docker stats

# CPU/메모리 사용량
docker stats --no-stream
```

### 디스크 사용량 확인

```bash
# Docker 전체 사용량
docker system df

# 볼륨 사용량
docker volume ls
```

## 🗄️ 데이터 백업

### 데이터베이스 백업

#### PSMO PostgreSQL

```bash
docker exec psmo-postgres-prod pg_dump -U psmo psmo_community > backup_psmo_$(date +%Y%m%d).sql
```

### 볼륨 백업

```bash
# 전체 볼륨 목록
docker volume ls

# 특정 볼륨 백업 (예: PostgreSQL 데이터)
sudo tar -czf psmo_postgres_backup.tar.gz \
  /var/lib/docker/volumes/all_workspace_psmo_postgres_data
```

### 자동 백업 스크립트

```bash
#!/bin/bash
# backup.sh

BACKUP_DIR="/home/sentimentalhoon/backups"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# PSMO DB
docker exec psmo-postgres-prod pg_dump -U psmo psmo_community \
  > $BACKUP_DIR/psmo_$DATE.sql

# 7일 이상 된 백업 삭제
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete

echo "Backup completed: $DATE"
```

실행 권한 부여 및 Cron 등록:

```bash
chmod +x backup.sh
sudo crontab -e
# 매일 오전 2시 백업
0 2 * * * /home/sentimentalhoon/backup.sh
```

## 🔧 트러블슈팅

### 컨테이너 시작 실패

```bash
# 로그 확인
docker logs [container-name]

# 강제 재생성
sudo docker compose -f docker-compose.prod.yml up -d --force-recreate [service-name]

# 캐시 없이 재빌드
sudo docker compose -f docker-compose.prod.yml build --no-cache [service-name]
```

### 디스크 공간 부족

```bash
# 사용하지 않는 이미지 삭제
docker image prune -a

# 중지된 컨테이너 삭제
docker container prune

# 사용하지 않는 볼륨 삭제 (주의!)
docker volume prune

# 전체 정리
docker system prune -a --volumes
```

### 네트워크 문제

```bash
# 네트워크 재생성
sudo docker compose -f docker-compose.prod.yml down
sudo docker network prune
sudo docker compose -f docker-compose.prod.yml up -d
```

## 🛡️ 보안 체크리스트

- [ ] 모든 비밀번호를 강력하게 설정
- [ ] `.env` 파일 권한 확인 (`chmod 600 .env`)
- [ ] SSH 포트 변경 또는 키 기반 인증 사용
- [ ] 방화벽 활성화 및 필요한 포트만 개방
- [ ] SSL 인증서 자동 갱신 설정
- [ ] 정기적인 백업 스케줄 설정
- [ ] Docker 소켓 권한 제한
- [ ] 컨테이너 리소스 제한 설정

## 📈 성능 최적화

### Docker 리소스 제한

`docker-compose.prod.yml`에 추가:

```yaml
services:
  psmo-backend:
    deploy:
      resources:
        limits:
          cpus: "1.0"
          memory: 1G
        reservations:
          cpus: "0.5"
          memory: 512M
```

### Nginx 캐싱 설정

이미 적용됨 (`infrastructure/nginx/nginx.conf`)

### 데이터베이스 튜닝

PostgreSQL 설정 최적화 (필요 시):

```bash
# postgresql.conf 수정
docker exec -it psmo-postgres-prod bash
vi /var/lib/postgresql/data/postgresql.conf
```

## 🔄 롤백 절차

### 코드 롤백

```bash
# 이전 커밋으로 돌아가기
git log --oneline  # 커밋 해시 확인
git checkout [commit-hash]

# 재배포
sudo docker compose -f docker-compose.prod.yml up -d --build
```

### 데이터베이스 롤백

```bash
# 백업에서 복구
docker exec -i psmo-postgres-prod psql -U psmo psmo_community < backup_psmo_20251201.sql
```

## 📞 지원

문제 발생 시:

1. 로그 확인 (`docker logs [container-name]`)
2. GitHub Issues 등록
3. 문서 확인 (`docs/TROUBLESHOOTING.md`)
