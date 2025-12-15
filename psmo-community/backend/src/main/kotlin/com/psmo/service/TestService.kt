package com.psmo.service

import com.psmo.database.DatabaseConfig
import com.psmo.database.MinioConfig
import com.psmo.database.RedisConfig
import com.psmo.model.TestDataDTO
import com.psmo.model.TestDataTable
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.tryGetString
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.PutObjectArgs
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.ByteArrayInputStream

/**
 * 인프라 연결 상태를 점검하기 위한 진단 서비스.
 * TODO: 운영 노출을 막기 위해 관리자 전용 인증 또는 feature flag 를 연동할 것
 */
class TestService(
    private val config: ApplicationConfig
) {
    private val database by lazy { DatabaseConfig.connectToDatabase(config) }
    private val jedisPool by lazy { RedisConfig.createJedisPool(config) }
    private val minioClient by lazy { MinioConfig.createMinioClient(config) }
    private val minioBucket by lazy { config.tryGetString("minio.bucket") ?: "psmo" }

    /**
     * PostgreSQL 에 샘플 데이터를 쓰고 다시 읽어 DB 연결을 검증한다.
     */
    fun testPostgreSQL(): Map<String, Any> = transaction(database) {
        val testData = TestDataDTO(
            name = "test-${System.currentTimeMillis()}",
            value = "Hello PostgreSQL from Kotlin!"
        )

        val id = TestDataTable.insertAndGetId {
            it[name] = testData.name
            it[value] = testData.value
            it[createdAt] = testData.createdAt
        }

        val allData = TestDataTable.selectAll()
            .map { row ->
                mapOf(
                    "id" to row[TestDataTable.id].value,
                    "name" to row[TestDataTable.name],
                    "value" to row[TestDataTable.value],
                    "createdAt" to row[TestDataTable.createdAt].toString()
                )
            }

        mapOf(
            "status" to "success",
            "message" to "PostgreSQL connection successful",
            "saved" to mapOf(
                "id" to id.value,
                "name" to testData.name,
                "value" to testData.value,
                "createdAt" to testData.createdAt.toString()
            ),
            "count" to allData.size,
            "allData" to allData
        )
    }

    /**
     * Redis 에 임시 키를 쓰고 바로 읽어 연결/TTL 을 확인한다.
     */
    fun testRedis(): Map<String, Any> = jedisPool.resource.use { jedis ->
        val key = "test-key-${System.currentTimeMillis()}"
        val value = "Hello Redis from Kotlin!"

        jedis.setex(key, 300, value)
        val retrieved = jedis.get(key)

        mapOf(
            "status" to "success",
            "message" to "Redis connection successful",
            "key" to key,
            "value" to value,
            "retrieved" to retrieved,
            "match" to (value == retrieved)
        )
    }

    /**
     * MinIO 버킷 존재 여부를 확인하고 테스트 파일을 업로드한다.
     */
    fun testMinIO(): Map<String, Any> {
        val bucketName = minioBucket
        val fileName = "test-${System.currentTimeMillis()}.txt"
        val content = "Hello MinIO from Kotlin!"

        if (!minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build()
            )
        ) {
            minioClient.makeBucket(
                MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build()
            )
        }

        val contentBytes = content.toByteArray()
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(fileName)
                .stream(ByteArrayInputStream(contentBytes), contentBytes.size.toLong(), -1)
                .contentType("text/plain")
                .build()
        )

        return mapOf(
            "status" to "success",
            "message" to "MinIO connection successful",
            "bucket" to bucketName,
            "file" to fileName,
            "content" to content
        )
    }

    /**
     * 세 가지 테스트를 동시에 실행하고 성공/실패를 묶어서 반환한다.
     */
    fun testAll(): Map<String, Any> = mapOf(
        "postgres" to runCatching { testPostgreSQL() }.getOrElse {
            mapOf("status" to "error", "message" to it.message)
        },
        "redis" to runCatching { testRedis() }.getOrElse {
            mapOf("status" to "error", "message" to it.message)
        },
        "minio" to runCatching { testMinIO() }.getOrElse {
            mapOf("status" to "error", "message" to it.message)
        }
    )
}
