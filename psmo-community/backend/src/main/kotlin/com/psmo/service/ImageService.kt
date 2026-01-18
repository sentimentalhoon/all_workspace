package com.psmo.service

import com.psmo.database.MinioConfig
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.tryGetString
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import java.io.InputStream
import java.util.UUID

class ImageService(private val config: ApplicationConfig) {
    private val minioClient: MinioClient by lazy { MinioConfig.createMinioClient(config) }
    private val bucketName = config.tryGetString("minio.bucket") ?: "psmo"
    private val videoBucketName = "${bucketName}-videos"

    init {
        ensureBucketExists(bucketName)
        ensureBucketExists(videoBucketName)
    }
    
    private fun ensureBucketExists(bucket: String) {
        try {
            val found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build())
            }
            
            // Set Bucket Policy to Public Read
            val policy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": {"AWS": ["*"]},
                      "Action": ["s3:GetObject"],
                      "Resource": ["arn:aws:s3:::$bucket/*"]
                    }
                  ]
                }
            """.trimIndent()
            
            minioClient.setBucketPolicy(
                io.minio.SetBucketPolicyArgs.builder()
                    .bucket(bucket)
                    .config(policy)
                    .build()
            )
            
        } catch (e: Exception) {
            // Log but don't crash app start if MinIO is down, retry later
            e.printStackTrace()
        }
    }

    fun uploadImage(inputStream: InputStream, originalFileName: String, contentType: String): String {
        return uploadFile(bucketName, inputStream, originalFileName, contentType)
    }
    
    fun uploadVideo(inputStream: InputStream, originalFileName: String, contentType: String): String {
        return uploadFile(videoBucketName, inputStream, originalFileName, contentType)
    }

    private fun uploadFile(bucket: String, inputStream: InputStream, originalFileName: String, contentType: String): String {
        val extension = originalFileName.substringAfterLast('.', "")
        val filename = "${UUID.randomUUID()}.$extension"
        
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucket)
                .`object`(filename)
                .stream(inputStream, -1, 10485760) // Part size 10MB
                .contentType(contentType)
                .build()
        )
        
        // Construct public URL. 
        // Nginx should proxy /storage/ requests to MinIO
        return "/storage/$bucket/$filename" 
    }

    fun downloadFile(bucket: String, filename: String): InputStream? {
        try {
            return minioClient.getObject(
                io.minio.GetObjectArgs.builder()
                    .bucket(bucket)
                    .`object`(filename)
                    .build()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
