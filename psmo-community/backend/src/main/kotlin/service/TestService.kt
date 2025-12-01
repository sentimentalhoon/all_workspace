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
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.ByteArrayInputStream

class TestService(
    private val config: ApplicationConfig
) {
    private val database by lazy {
        DatabaseConfig.connectToDatabase(config).also { db ->
            transaction(db) {
                SchemaUtils.create(TestDataTable)
            }
        }
    }
    private val jedisPool by lazy { RedisConfig.createJedisPool(config) }
    private val minioClient by lazy { MinioConfig.createMinioClient(config) }
    private val minioBucket by lazy { config.tryGetString("minio.bucket") ?: "psmo" }

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
