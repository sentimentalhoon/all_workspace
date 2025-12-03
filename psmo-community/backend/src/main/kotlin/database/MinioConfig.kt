package com.psmo.database

import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.tryGetString
import io.minio.MinioClient

/**
 * MinIO 클라이언트 생성을 담당.
 */
object MinioConfig {
    /**
     * 애플리케이션 설정/환경변수 기반으로 MinIO 엔드포인트를 초기화한다.
     * TODO: TLS 인증서 적용 시 endpoint scheme 을 동적으로 처리
     */
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
