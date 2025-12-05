# SSL 인증서 설치 가이드

## 📁 Certbot으로 자동 발급된 인증서 위치

```
/etc/letsencrypt/live/mycommunity.duckdns.org/
├── fullchain.pem
├── privkey.pem
├── cert.pem
└── chain.pem
```

## 🔐 SSL 인증서 발급 방법

### 옵션 1: Certbot으로 자동 발급 (권장)

```bash
# Certbot 설치 (Ubuntu)
sudo apt update
sudo apt install certbot python3-certbot-nginx

# 인증서 발급
sudo certbot certonly --standalone \
  -d mycommunity.duckdns.org \
  --preferred-challenges http
```

**주의**: 발급 시 80 포트가 비어있어야 합니다. Docker 실행 전에 발급받으세요!

### 옵션 2: 기존 인증서가 있는 경우

수동으로 배치:

```bash
# 디렉토리 생성
sudo mkdir -p /etc/letsencrypt/live/mycommunity.duckdns.org

# 인증서 복사
sudo cp /your/cert/fullchain.pem /etc/letsencrypt/live/mycommunity.duckdns.org/
sudo cp /your/cert/privkey.pem /etc/letsencrypt/live/mycommunity.duckdns.org/

# 권한 설정
sudo chmod 644 /etc/letsencrypt/live/*/fullchain.pem
sudo chmod 600 /etc/letsencrypt/live/*/privkey.pem
```

## 🚀 실행 방법

### 1. 인증서 확인

```bash
# 인증서 존재 확인
sudo ls -la /etc/letsencrypt/live/mycommunity.duckdns.org/

# 인증서 정보 확인
sudo openssl x509 -in /etc/letsencrypt/live/mycommunity.duckdns.org/fullchain.pem -text -noout
```

### 2. 환경 변수 설정

```bash
cp .env.prod.example .env
# .env 파일을 열어서 모든 비밀번호를 변경하세요!
nano .env
```

### 3. Docker Compose 실행

```bash
# 빌드 및 실행 (인증서는 자동으로 마운트됨)
docker compose -f docker-compose.prod.yml up --build -d

# 로그 확인
docker compose -f docker-compose.prod.yml logs -f nginx

# Nginx 설정 테스트
docker compose -f docker-compose.prod.yml exec nginx nginx -t
```

### 4. 접속 확인

- PSMO Community: https://mycommunity.duckdns.org

## 🔄 자동 갱신 설정

Let's Encrypt 인증서는 90일마다 갱신이 필요합니다.

### Certbot 자동 갱신

```bash
# 갱신 테스트
sudo certbot renew --dry-run

# 자동 갱신은 systemd timer로 자동 설정됨
sudo systemctl status certbot.timer

# 수동 갱신
sudo certbot renew

# 갱신 후 Nginx 재시작
docker compose -f docker-compose.prod.yml restart nginx
```

### Cron으로 자동 갱신 + Nginx 재시작

```bash
# Crontab 편집
sudo crontab -e

# 매일 새벽 2시에 갱신 시도, 갱신되면 Nginx 재시작
0 2 * * * certbot renew --quiet && docker compose -f /path/to/your/project/docker-compose.prod.yml restart nginx
```

### Docker Compose에 Certbot 추가 (선택사항)

`docker-compose.prod.yml`에 추가:

```yaml
certbot:
  image: certbot/certbot:latest
  container_name: certbot
  volumes:
    - /etc/letsencrypt:/etc/letsencrypt
    - /var/lib/letsencrypt:/var/lib/letsencrypt
    - certbot_www:/var/www/certbot
  entrypoint: "/bin/sh -c 'trap exit TERM; while :; do certbot renew --webroot -w /var/www/certbot; sleep 12h & wait $${!}; done;'"
  networks:
    - proxy-network
```

## ⚠️ 문제 해결

### 인증서 경로 확인

```bash
# 호스트에서 인증서 확인
sudo ls -la /etc/letsencrypt/live/mycommunity.duckdns.org/

# 컨테이너 내부에서 확인
docker compose -f docker-compose.prod.yml exec nginx ls -la /etc/nginx/ssl/psmo/
```

### SSL 인증서 오류

```bash
# Nginx 설정 테스트
docker compose -f docker-compose.prod.yml exec nginx nginx -t

# Nginx 재시작
docker compose -f docker-compose.prod.yml restart nginx

# 로그 확인
docker compose -f docker-compose.prod.yml logs nginx
```

### 권한 오류

```bash
# 인증서 권한 확인
sudo ls -l /etc/letsencrypt/live/mycommunity.duckdns.org/

# 필요시 권한 조정 (보통 자동으로 올바르게 설정됨)
sudo chmod 644 /etc/letsencrypt/live/*/fullchain.pem
sudo chmod 600 /etc/letsencrypt/live/*/privkey.pem
```

## 📝 인증서 정보 확인

```bash
# PSMO 인증서
openssl x509 -in infrastructure/nginx/ssl/psmo/fullchain.pem -text -noout
```
