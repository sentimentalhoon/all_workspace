# SSL 인증서 설치 가이드

## 📁 디렉토리 구조

```
infrastructure/nginx/ssl/
├── campstation/
│   ├── fullchain.pem
│   └── privkey.pem
└── psmo/
    ├── fullchain.pem
    └── privkey.pem
```

## 🔐 SSL 인증서 설치 방법

### 1. 디렉토리 생성

```bash
mkdir -p infrastructure/nginx/ssl/campstation
mkdir -p infrastructure/nginx/ssl/psmo
```

### 2. 인증서 파일 복사

이미 받은 SSL 인증서를 다음 위치에 복사하세요:

**Campstation (mycamp.duckdns.org):**
```bash
# 인증서 체인 파일
cp /path/to/your/campstation/fullchain.pem infrastructure/nginx/ssl/campstation/
# 개인키 파일
cp /path/to/your/campstation/privkey.pem infrastructure/nginx/ssl/campstation/
```

**PSMO Community (mycommunity.duckdns.org):**
```bash
# 인증서 체인 파일
cp /path/to/your/psmo/fullchain.pem infrastructure/nginx/ssl/psmo/
# 개인키 파일
cp /path/to/your/psmo/privkey.pem infrastructure/nginx/ssl/psmo/
```

### 3. 권한 설정 (Linux/Mac)

```bash
chmod 644 infrastructure/nginx/ssl/campstation/fullchain.pem
chmod 600 infrastructure/nginx/ssl/campstation/privkey.pem
chmod 644 infrastructure/nginx/ssl/psmo/fullchain.pem
chmod 600 infrastructure/nginx/ssl/psmo/privkey.pem
```

## 🚀 실행 방법

### 1. 환경 변수 설정

```bash
cp .env.prod.example .env
# .env 파일을 열어서 모든 비밀번호를 변경하세요!
```

### 2. Docker Compose 실행

```bash
# 빌드 및 실행
docker compose -f docker-compose.prod.yml up --build -d

# 로그 확인
docker compose -f docker-compose.prod.yml logs -f

# 특정 서비스 로그만 확인
docker compose -f docker-compose.prod.yml logs -f nginx
```

### 3. 접속 확인

- Campstation: https://mycamp.duckdns.org
- PSMO Community: https://mycommunity.duckdns.org

## 🔄 자동 갱신 (Let's Encrypt 사용시)

Let's Encrypt를 사용하는 경우, Certbot으로 자동 갱신 설정:

```bash
# Certbot 컨테이너 추가 (docker-compose.prod.yml에)
certbot:
  image: certbot/certbot:latest
  volumes:
    - ./infrastructure/nginx/ssl:/etc/letsencrypt
    - certbot_www:/var/www/certbot
  entrypoint: "/bin/sh -c 'trap exit TERM; while :; do certbot renew; sleep 12h & wait $${!}; done;'"
```

## ⚠️ 문제 해결

### SSL 인증서 오류
```bash
# Nginx 설정 테스트
docker compose -f docker-compose.prod.yml exec nginx nginx -t

# Nginx 재시작
docker compose -f docker-compose.prod.yml restart nginx
```

### 인증서 경로 확인
```bash
# 컨테이너 내부 확인
docker compose -f docker-compose.prod.yml exec nginx ls -la /etc/nginx/ssl/campstation/
docker compose -f docker-compose.prod.yml exec nginx ls -la /etc/nginx/ssl/psmo/
```

## 📝 인증서 정보 확인

```bash
# Campstation 인증서
openssl x509 -in infrastructure/nginx/ssl/campstation/fullchain.pem -text -noout

# PSMO 인증서
openssl x509 -in infrastructure/nginx/ssl/psmo/fullchain.pem -text -noout
```
