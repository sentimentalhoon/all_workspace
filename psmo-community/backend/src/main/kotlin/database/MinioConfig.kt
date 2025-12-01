package com.psmo.database

import io.minio.MinioClient

object MinioConfig {
    fun createMinioClient(
        endpoint: String = System.getenv("MINIO_ENDPOINT") ?: "http://psmo-minio:9000",
        accessKey: String = System.getenv("MINIO_ACCESS_KEY") ?: "psmo_minio_key",
        secretKey: String = System.getenv("MINIO_SECRET_KEY") ?: "psmo_minio_secret"
    ): MinioClient = MinioClient.builder()
        .endpoint(endpoint)
        .credentials(accessKey, secretKey)
        .build()
}
