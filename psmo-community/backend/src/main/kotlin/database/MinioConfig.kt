package com.psmo.database

import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.tryGetString
import io.minio.MinioClient

object MinioConfig {
    fun createMinioClient(config: ApplicationConfig): MinioClient {
        val endpoint = config.tryGetString("minio.endpoint")
            ?: System.getenv("MINIO_ENDPOINT")
            ?: "http://psmo-minio:9000"
        val accessKey = config.tryGetString("minio.accessKey")
            ?: System.getenv("MINIO_ACCESS_KEY")
            ?: "psmo_minio_key"
        val secretKey = config.tryGetString("minio.secretKey")
            ?: System.getenv("MINIO_SECRET_KEY")
            ?: "psmo_minio_secret"

        return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build()
    }
}
