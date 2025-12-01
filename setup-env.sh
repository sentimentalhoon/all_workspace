#!/bin/bash

# =============================================================================
# Production Environment Setup Script
# =============================================================================
# This script creates .env.prod file with secure random passwords
# Usage: ./setup-env.sh
# =============================================================================

set -e

echo "=================================================="
echo "  Production Environment Setup"
echo "=================================================="
echo ""

# Check if .env.prod already exists
if [ -f ".env.prod" ]; then
    echo "⚠️  .env.prod already exists!"
    read -p "Do you want to overwrite it? (yes/no): " -r
    echo
    if [[ ! $REPLY =~ ^[Yy]es$ ]]; then
        echo "❌ Setup cancelled."
        exit 1
    fi
    echo "🗑️  Removing existing .env.prod..."
    rm .env.prod
fi

# Function to generate secure password
generate_password() {
    openssl rand -base64 32 | tr -d "=+/" | cut -c1-32
}

echo "📧 Enter your email for SSL certificate notifications:"
read -p "Email: " CERTBOT_EMAIL

if [ -z "$CERTBOT_EMAIL" ]; then
    echo "❌ Email is required!"
    exit 1
fi

echo ""
echo "🔐 Generating secure passwords..."

# Generate passwords
CAMPSTATION_POSTGRES_PASSWORD=$(generate_password)
CAMPSTATION_REDIS_PASSWORD=$(generate_password)
CAMPSTATION_MINIO_PASSWORD=$(generate_password)
PSMO_POSTGRES_PASSWORD=$(generate_password)
PSMO_REDIS_PASSWORD=$(generate_password)
PSMO_MINIO_PASSWORD=$(generate_password)

# Create .env.prod file
cat > .env.prod << EOF
# =============================================================================
# Production Environment Variables
# =============================================================================
# Generated on: $(date)
# DO NOT commit this file to git!

# =============================================================================
# Domain Configuration
# =============================================================================
CAMPSTATION_DOMAIN=mycamp.duckdns.org
PSMO_DOMAIN=mycommunity.duckdns.org

# =============================================================================
# SSL/TLS Configuration
# =============================================================================
CERTBOT_EMAIL=${CERTBOT_EMAIL}

# =============================================================================
# Campstation - PostgreSQL Database
# =============================================================================
CAMPSTATION_POSTGRES_DB=campstation
CAMPSTATION_POSTGRES_USER=campstation
CAMPSTATION_POSTGRES_PASSWORD=${CAMPSTATION_POSTGRES_PASSWORD}

# =============================================================================
# Campstation - Redis Cache
# =============================================================================
CAMPSTATION_REDIS_PASSWORD=${CAMPSTATION_REDIS_PASSWORD}

# =============================================================================
# Campstation - MinIO Object Storage
# =============================================================================
CAMPSTATION_MINIO_USER=minioadmin
CAMPSTATION_MINIO_PASSWORD=${CAMPSTATION_MINIO_PASSWORD}

# =============================================================================
# Campstation - CORS Configuration
# =============================================================================
CAMPSTATION_CORS_ORIGINS=https://mycamp.duckdns.org

# =============================================================================
# PSMO Community - PostgreSQL Database
# =============================================================================
PSMO_POSTGRES_DB=psmo_community
PSMO_POSTGRES_USER=psmo
PSMO_POSTGRES_PASSWORD=${PSMO_POSTGRES_PASSWORD}

# =============================================================================
# PSMO Community - Redis Cache
# =============================================================================
PSMO_REDIS_PASSWORD=${PSMO_REDIS_PASSWORD}

# =============================================================================
# PSMO Community - MinIO Object Storage
# =============================================================================
PSMO_MINIO_USER=minioadmin
PSMO_MINIO_PASSWORD=${PSMO_MINIO_PASSWORD}

# =============================================================================
# PSMO Community - CORS Configuration
# =============================================================================
PSMO_CORS_ORIGINS=https://mycommunity.duckdns.org
EOF

# Set secure permissions
chmod 600 .env.prod

echo ""
echo "✅ .env.prod file created successfully!"
echo ""
echo "=================================================="
echo "  Generated Credentials Summary"
echo "=================================================="
echo ""
echo "📧 SSL Email: ${CERTBOT_EMAIL}"
echo ""
echo "🔐 Campstation Passwords:"
echo "   PostgreSQL: ${CAMPSTATION_POSTGRES_PASSWORD}"
echo "   Redis:      ${CAMPSTATION_REDIS_PASSWORD}"
echo "   MinIO:      ${CAMPSTATION_MINIO_PASSWORD}"
echo ""
echo "🔐 PSMO Passwords:"
echo "   PostgreSQL: ${PSMO_POSTGRES_PASSWORD}"
echo "   Redis:      ${PSMO_REDIS_PASSWORD}"
echo "   MinIO:      ${PSMO_MINIO_PASSWORD}"
echo ""
echo "=================================================="
echo ""
echo "⚠️  IMPORTANT: Save these passwords in a secure location!"
echo "⚠️  File permissions set to 600 (owner read/write only)"
echo ""
echo "Next steps:"
echo "  1. Review the .env.prod file"
echo "  2. Start services: docker compose --env-file .env.prod -f docker-compose.prod.yml up -d"
echo ""
