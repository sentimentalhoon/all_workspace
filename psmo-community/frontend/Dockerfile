# Production Dockerfile for PSMO Nuxt Frontend
FROM node:22-alpine AS build

WORKDIR /app

# Copy package files
COPY package.json package-lock.json* ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build arguments (if needed for build-time config, though Nuxt runtime config is preferred)
ARG VITE_TELEGRAM_BOT_ID
ARG VITE_TELEGRAM_BOT_USERNAME
ENV VITE_TELEGRAM_BOT_ID=${VITE_TELEGRAM_BOT_ID}
ENV VITE_TELEGRAM_BOT_USERNAME=${VITE_TELEGRAM_BOT_USERNAME}

# Build application
RUN npm run build

# Production runtime stage
FROM node:22-alpine

WORKDIR /app

# Copy built artifacts from build stage
COPY --from=build /app/.output ./.output

# Set environment variables for Nitro server
ENV NUXT_HOST=0.0.0.0
ENV NUXT_PORT=3000

# Expose port
EXPOSE 3000

# Start server
CMD ["node", ".output/server/index.mjs"]
