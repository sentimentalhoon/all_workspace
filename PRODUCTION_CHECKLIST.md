# Production Deployment Checklist

## ✅ Pre-Deployment Checklist

### 1. Environment Variables

- [ ] Copy `.env.prod.example` to `.env.prod`
- [ ] Update all `CHANGE_ME_*` passwords with strong passwords
- [ ] Set correct domain names for CORS
- [ ] Configure API URLs for frontend builds
- [ ] Verify all required environment variables are set

### 2. Database Configuration

- [ ] PostgreSQL passwords are strong and unique
- [ ] Database names are appropriate for production
- [ ] JPA ddl-auto is set to `validate` (not `update` or `create-drop`)
- [ ] SQL logging is disabled (`show-sql: false`)

### 3. Security

- [ ] Redis passwords are configured
- [ ] MinIO credentials are set
- [ ] CORS origins are restricted to production domains only
- [ ] SSL/TLS certificates are ready (if using HTTPS)
- [ ] Security headers are configured in Nginx

### 4. Backend Configuration

#### Campstation (Spring Boot)

- [ ] `SPRING_PROFILES_ACTIVE=prod`
- [ ] `SPRING_JPA_DDL_AUTO=validate`
- [ ] `SPRING_JPA_SHOW_SQL=false`
- [ ] CORS origins match production frontend URL
- [ ] Database connection pooling configured
- [ ] Health check endpoint accessible

#### PSMO (Ktor)

- [ ] `KTOR_PROFILE=prod`
- [ ] CORS configuration matches production frontend URL
- [ ] Database connection configured
- [ ] Redis and MinIO endpoints correct
- [ ] Health check endpoint accessible

### 5. Frontend Configuration

- [ ] API URLs point to production backend
- [ ] Build arguments set correctly in docker-compose
- [ ] Static assets properly served by Nginx
- [ ] Gzip compression enabled
- [ ] Cache headers configured

### 6. Infrastructure

- [ ] Docker volumes for data persistence
- [ ] Nginx reverse proxy configured
- [ ] SSL certificates installed (if applicable)
- [ ] Health checks configured for all services
- [ ] Restart policies set to `unless-stopped`

## 🚀 Deployment Steps

### 1. Prepare Environment

```bash
# Copy and configure environment file
cp .env.prod.example .env.prod
nano .env.prod  # Edit with production values
```

### 2. Build and Start Services

```bash
# Build images
docker compose -f docker-compose.prod.yml build --no-cache

# Start services
docker compose -f docker-compose.prod.yml up -d

# Check service status
docker compose -f docker-compose.prod.yml ps
```

### 3. Verify Deployment

```bash
# Check backend health (through Nginx proxy)
curl https://mycamp.duckdns.org/api/health
curl https://mycommunity.duckdns.org/api/health

# Check database connections
docker compose -f docker-compose.prod.yml logs campstation-backend
docker compose -f docker-compose.prod.yml logs psmo-backend

# Verify all services are healthy
docker compose -f docker-compose.prod.yml ps
```

### 4. Monitor Services

```bash
# View logs
docker compose -f docker-compose.prod.yml logs -f

# Check specific service
docker compose -f docker-compose.prod.yml logs -f campstation-backend
docker compose -f docker-compose.prod.yml logs -f psmo-backend
```

## 🔍 Post-Deployment Verification

### Health Checks

- [ ] Campstation backend: `/api/health` returns 200
- [ ] PSMO backend: `/api/health` returns 200
- [ ] PostgreSQL connections working
- [ ] Redis connections working
- [ ] MinIO connections working

### Database Tests

- [ ] Campstation: `/api/test/all` succeeds
- [ ] PSMO: `/api/test/all` succeeds
- [ ] Data persists across container restarts

### Frontend Verification

- [ ] Campstation frontend loads correctly
- [ ] PSMO frontend loads correctly
- [ ] API calls work from frontend
- [ ] CORS is not blocking requests
- [ ] Static assets load properly

### Security Verification

- [ ] HTTPS is working (if configured)
- [ ] CORS only allows production domains
- [ ] Database ports not exposed externally
- [ ] Redis ports not exposed externally
- [ ] MinIO console not publicly accessible

## 🚨 Common Issues and Solutions

### CORS Errors

**Problem**: Frontend cannot access backend API

**Solution**:

1. Check `CORS_ALLOWED_ORIGINS` environment variable
2. Ensure it matches your frontend domain exactly
3. Include protocol (http:// or https://)
4. Restart backend service after changes

### Database Connection Errors

**Problem**: Backend cannot connect to database

**Solution**:

1. Verify environment variables match docker-compose configuration
2. Check `DB_URL`, `DB_USER`, `DB_PASSWORD`
3. Ensure PostgreSQL container is healthy
4. Check network connectivity between containers

### Health Check Failures

**Problem**: Services show as unhealthy

**Solution**:

1. Check if backend is listening on correct port (8080)
2. Verify `/api/health` endpoint is accessible
3. Check container logs for startup errors
4. Increase health check timeout if needed

### Frontend Build Errors

**Problem**: Frontend container fails to build

**Solution**:

1. Ensure `VITE_API_URL` build arg is set
2. Check node_modules and package-lock.json
3. Clear Docker build cache: `docker builder prune`

## 📝 Environment Variables Reference

### Required Variables

All variables marked with `:?` in docker-compose.prod.yml are **required**:

- `CAMPSTATION_POSTGRES_PASSWORD`
- `CAMPSTATION_REDIS_PASSWORD`
- `CAMPSTATION_MINIO_USER`
- `CAMPSTATION_MINIO_PASSWORD`
- `PSMO_POSTGRES_PASSWORD`
- `PSMO_REDIS_PASSWORD`
- `PSMO_MINIO_USER`
- `PSMO_MINIO_PASSWORD`

### Optional Variables (with defaults)

- `CAMPSTATION_POSTGRES_DB` (default: campstation)
- `CAMPSTATION_POSTGRES_USER` (default: campstation)
- `CAMPSTATION_CORS_ORIGINS` (default: https://mycamp.duckdns.org)
- `CAMPSTATION_API_URL` (default: https://mycamp.duckdns.org)
- `PSMO_POSTGRES_DB` (default: psmo_community)
- `PSMO_POSTGRES_USER` (default: psmo)
- `PSMO_CORS_ORIGINS` (default: https://mycommunity.duckdns.org)
- `PSMO_API_URL` (default: https://mycommunity.duckdns.org)

## 🔄 Updates and Rollbacks

### Update Services

```bash
# Pull latest code
git pull origin main

# Rebuild specific service
docker compose -f docker-compose.prod.yml build campstation-backend
docker compose -f docker-compose.prod.yml up -d campstation-backend

# Or rebuild all
docker compose -f docker-compose.prod.yml build
docker compose -f docker-compose.prod.yml up -d
```

### Rollback

```bash
# Stop services
docker compose -f docker-compose.prod.yml down

# Checkout previous version
git checkout <previous-commit>

# Rebuild and restart
docker compose -f docker-compose.prod.yml build
docker compose -f docker-compose.prod.yml up -d
```

## 📊 Monitoring

### Check Resource Usage

```bash
# Container stats
docker stats

# Disk usage
docker system df

# Logs size
docker compose -f docker-compose.prod.yml logs --tail=100
```

### Database Backups

```bash
# Backup Campstation database
docker exec campstation-postgres-prod pg_dump -U campstation campstation > backup_campstation_$(date +%Y%m%d).sql

# Backup PSMO database
docker exec psmo-postgres-prod pg_dump -U psmo psmo_community > backup_psmo_$(date +%Y%m%d).sql
```

## 🆘 Emergency Procedures

### Service Down

```bash
# Restart specific service
docker compose -f docker-compose.prod.yml restart campstation-backend

# Restart all services
docker compose -f docker-compose.prod.yml restart
```

### Complete System Restart

```bash
# Stop all services (preserves data)
docker compose -f docker-compose.prod.yml down

# Start all services
docker compose -f docker-compose.prod.yml up -d
```

### Data Recovery

```bash
# Restore database from backup
docker exec -i campstation-postgres-prod psql -U campstation campstation < backup_campstation_20250101.sql
```
