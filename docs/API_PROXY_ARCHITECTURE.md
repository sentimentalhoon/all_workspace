# API Proxy Architecture

## 🏗️ Architecture Overview

### Domain Structure

- **Campstation**

  - Frontend: `https://mycamp.duckdns.org`
  - Backend API: `https://mycamp.duckdns.org/api`

- **PSMO Community**
  - Frontend: `https://mycommunity.duckdns.org`
  - Backend API: `https://mycommunity.duckdns.org/api`

### Nginx Reverse Proxy

```
Client → Nginx (443) → Frontend Container (80)
                    ↘ Backend Container (8080) [/api path]
```

## ✅ Advantages of /api Proxy Pattern

### 1. **Single SSL Certificate per Domain**

- ❌ Without proxy: Need 2 certificates per project (frontend + api subdomain)
- ✅ With proxy: Only 1 certificate per domain
- **Cost**: Free with Let's Encrypt, but simpler management
- **Example**: `mycamp.duckdns.org` covers both frontend and `/api`

### 2. **No CORS Issues**

- ❌ Subdomain (`api.mycamp.duckdns.org`): CORS headers required
- ✅ Same origin (`mycamp.duckdns.org/api`): No CORS needed!
- **Benefit**: Simpler configuration, fewer security concerns

### 3. **Easier SSL/TLS Management**

```bash
# Only 2 certificates needed total
certbot certonly --webroot -w /var/www/certbot \
  -d mycamp.duckdns.org \
  -d mycommunity.duckdns.org

# vs 4 certificates with subdomain approach
# -d mycamp.duckdns.org
# -d api.mycamp.duckdns.org
# -d mycommunity.duckdns.org
# -d api.mycommunity.duckdns.org
```

### 4. **Simplified DNS Configuration**

- Only need 2 A records:
  ```
  mycamp.duckdns.org → Your IP
  mycommunity.duckdns.org → Your IP
  ```

### 5. **Better Mobile/App Integration**

- Mobile apps work seamlessly with same-origin requests
- No need to configure CORS for native apps
- Simpler API endpoint configuration

### 6. **Easier Development-to-Production Transition**

```javascript
// Development
const API_URL = "http://localhost:8080";

// Production - Just change domain, path stays same
const API_URL = "https://mycamp.duckdns.org";

// API calls always use /api prefix
fetch(`${API_URL}/api/health`);
```

## 🔧 Configuration Details

### Nginx Configuration

Located in `infrastructure/nginx/conf.d/`

#### Campstation (`campstation.conf`)

```nginx
# Frontend - Serves Vue.js app
location / {
    proxy_pass http://campstation-frontend:80;
}

# Backend API - Proxies to Spring Boot
location /api {
    proxy_pass http://campstation-backend:8080;
}
```

#### PSMO (`psmo-community.conf`)

```nginx
# Frontend - Serves Vue.js app
location / {
    proxy_pass http://psmo-frontend:80;
}

# Backend API - Proxies to Ktor
location /api {
    proxy_pass http://psmo-backend:8080;
}
```

### Environment Variables

#### Production (.env.prod)

```bash
# Campstation
CAMPSTATION_CORS_ORIGINS=https://mycamp.duckdns.org
CAMPSTATION_API_URL=https://mycamp.duckdns.org

# PSMO
PSMO_CORS_ORIGINS=https://mycommunity.duckdns.org
PSMO_API_URL=https://mycommunity.duckdns.org
```

### Frontend Configuration

#### Campstation

```typescript
// vite.config.ts - proxy for development
export default defineConfig({
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
```

```vue
// Components - Use relative or absolute URLs const apiBaseUrl =
import.meta.env.VITE_API_URL || 'http://localhost:8080'
fetch(`${apiBaseUrl}/api/test/all`)
```

## 🔒 Security Benefits

### 1. **Same-Origin Policy Protection**

- Browser enforces stricter security
- No preflight OPTIONS requests needed
- Cookies/credentials work seamlessly

### 2. **Simplified CORS Configuration**

```java
// Spring Boot - Only allow same origin
@Value("${cors.allowed-origins:https://mycamp.duckdns.org}")
private String allowedOrigins;
```

### 3. **Rate Limiting per Path**

```nginx
# Different limits for frontend vs API
location / {
    limit_req zone=general burst=20 nodelay;
}

location /api {
    limit_req zone=api burst=50 nodelay;
}
```

## 🚀 Deployment Workflow

### 1. DNS Setup (DuckDNS)

```bash
# Add both domains to DuckDNS
mycamp.duckdns.org
mycommunity.duckdns.org
```

### 2. SSL Certificate Generation

```bash
# Single command for both domains
docker compose -f docker-compose.prod.yml run --rm certbot certonly \
  --webroot -w /var/www/certbot \
  -d mycamp.duckdns.org \
  -d mycommunity.duckdns.org \
  --email your-email@example.com \
  --agree-tos \
  --no-eff-email
```

### 3. Build and Deploy

```bash
# Set environment variables
cp .env.prod.example .env.prod
nano .env.prod

# Build with correct API URLs
docker compose -f docker-compose.prod.yml build

# Start services
docker compose -f docker-compose.prod.yml up -d
```

### 4. Verify

```bash
# Test frontend
curl https://mycamp.duckdns.org

# Test API through proxy
curl https://mycamp.duckdns.org/api/health

# Should return: {"status":"UP","service":"Campstation Backend"}
```

## 📊 Performance Benefits

### 1. **HTTP/2 Connection Reuse**

- Same TCP connection for frontend and API
- Faster requests, reduced latency

### 2. **Shared SSL Session**

- No additional SSL handshake for API calls
- Reduced overhead

### 3. **Browser Caching**

- Service workers can cache API responses
- Better offline support

## 🔄 Comparison Table

| Feature             | Subdomain Approach      | /api Proxy Approach     |
| ------------------- | ----------------------- | ----------------------- |
| SSL Certificates    | 4 total (2 per project) | 2 total (1 per project) |
| CORS Configuration  | Required + Complex      | Not needed / Simple     |
| DNS Records         | 4 A records             | 2 A records             |
| Certificate Renewal | 4 renewals              | 2 renewals              |
| Mobile App Config   | 2 endpoints             | 1 endpoint              |
| Cookie Sharing      | ❌ Cross-domain         | ✅ Same-domain          |
| Development Proxy   | Complex                 | Simple                  |
| Security            | Good                    | Better                  |

## 🎯 Best Practices

### 1. **API Versioning**

```nginx
# Support multiple API versions
location /api/v1 {
    proxy_pass http://backend:8080/api/v1;
}

location /api/v2 {
    proxy_pass http://backend:8080/api/v2;
}
```

### 2. **Health Check Endpoints**

```nginx
# Exclude health checks from rate limiting
location = /api/health {
    limit_req off;
    proxy_pass http://backend:8080/api/health;
}
```

### 3. **Static Asset Separation**

```nginx
# CDN for heavy assets
location /uploads {
    proxy_pass http://minio:9000;
}

# API for dynamic content
location /api {
    proxy_pass http://backend:8080;
}
```

### 4. **Request Size Limits**

```nginx
# Different limits for different endpoints
location /api/upload {
    client_max_body_size 100M;
    proxy_pass http://backend:8080;
}

location /api {
    client_max_body_size 10M;
    proxy_pass http://backend:8080;
}
```

## 🐛 Troubleshooting

### Issue: 502 Bad Gateway

```bash
# Check backend is running
docker compose -f docker-compose.prod.yml ps

# Check backend logs
docker logs campstation-backend-prod

# Test direct backend connection
docker exec -it nginx-proxy curl http://campstation-backend:8080/api/health
```

### Issue: CORS errors still appearing

```bash
# Verify CORS is set to same domain
echo $CAMPSTATION_CORS_ORIGINS
# Should be: https://mycamp.duckdns.org

# Check Nginx is proxying correctly
curl -H "Origin: https://mycamp.duckdns.org" https://mycamp.duckdns.org/api/health -v
```

### Issue: SSL certificate not working

```bash
# Verify certificate files exist
docker exec nginx-proxy ls -la /etc/letsencrypt/live/mycamp.duckdns.org/

# Test SSL
curl -v https://mycamp.duckdns.org/api/health
```

## 📚 References

- [Nginx Reverse Proxy Guide](https://docs.nginx.com/nginx/admin-guide/web-server/reverse-proxy/)
- [Let's Encrypt Best Practices](https://letsencrypt.org/docs/integration-guide/)
- [DuckDNS Documentation](https://www.duckdns.org/spec.jsp)
