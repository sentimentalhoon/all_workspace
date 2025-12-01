# Production Environment Setup

This directory contains scripts to automatically set up production environment variables.

## Quick Start

### On Linux/macOS/Ubuntu Server

```bash
# Make the script executable
chmod +x setup-env.sh

# Run the setup script
./setup-env.sh
```

The script will:
1. Ask for your email address (for SSL certificate notifications)
2. Generate secure random passwords for all services
3. Create `.env.prod` file with all configurations
4. Set secure file permissions (600)
5. Display all generated credentials

### Manual Setup

If you prefer to set up manually:

```bash
# Copy the example file
cp .env.prod.example .env.prod

# Edit with your values
nano .env.prod

# Set secure permissions
chmod 600 .env.prod
```

## After Setup

Once `.env.prod` is created, start the services:

```bash
docker compose --env-file .env.prod -f docker-compose.prod.yml up -d
```

## Security Notes

- ✅ `.env.prod` is in `.gitignore` - never commit it
- ✅ File permissions are set to 600 (owner only)
- ✅ All passwords are randomly generated (32 characters)
- ⚠️ Save the displayed credentials in a secure password manager
- ⚠️ Keep the `.env.prod` file secure on the server only

## What Gets Generated

The script automatically generates secure passwords for:

### Campstation Services
- PostgreSQL database password
- Redis cache password
- MinIO object storage password

### PSMO Community Services
- PostgreSQL database password
- Redis cache password
- MinIO object storage password

All passwords are:
- 32 characters long
- Cryptographically random
- URL-safe (no special characters that need escaping)

## Troubleshooting

**Script permission denied:**
```bash
chmod +x setup-env.sh
```

**OpenSSL not found:**
```bash
# Ubuntu/Debian
sudo apt-get install openssl

# macOS (should be pre-installed)
brew install openssl
```

**Want to regenerate passwords:**
```bash
# Remove the old file
rm .env.prod

# Run setup again
./setup-env.sh
```
