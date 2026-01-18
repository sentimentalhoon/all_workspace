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
    
    fun uploadImageBytes(bytes: ByteArray, originalFileName: String, contentType: String): String {
        return bytes.inputStream().use { inputStream ->
            uploadFile(bucketName, inputStream, originalFileName, contentType)
        }
    }

    data class ImageUploadResult(val originalUrl: String, val thumbnailUrl: String)

    fun uploadImageWithThumbnail(bytes: ByteArray, originalFileName: String, contentType: String): ImageUploadResult {
        // 1. Upload Original
        val originalUrl = uploadImageBytes(bytes, originalFileName, contentType)

        // 2. Generate Thumbnail (Resize to max width 300px)
        val thumbnailBytes = createThumbnail(bytes, 300)
        
        // 3. Upload Thumbnail (append _thumb)
        val ext = originalFileName.substringAfterLast('.', "")
        val nameWithoutExt = originalFileName.substringBeforeLast('.')
        val thumbName = "${nameWithoutExt}_thumb.$ext"
        
        val thumbnailUrl = uploadImageBytes(thumbnailBytes, thumbName, contentType)
        
        return ImageUploadResult(originalUrl, thumbnailUrl)
    }

    private fun createThumbnail(bytes: ByteArray, maxWidth: Int): ByteArray {
        try {
            val inputStream = java.io.ByteArrayInputStream(bytes)
            val originalImage = javax.imageio.ImageIO.read(inputStream) ?: return bytes

            val width = originalImage.width
            val height = originalImage.height

            if (width <= maxWidth) return bytes // No resize needed

            val newWidth = maxWidth
            val newHeight = (height * newWidth) / width

            val resizedImage = java.awt.image.BufferedImage(newWidth, newHeight, java.awt.image.BufferedImage.TYPE_INT_RGB)
            val graphics = resizedImage.createGraphics()
            graphics.drawImage(originalImage, 0, 0, newWidth, newHeight, null)
            graphics.dispose()

            val outputStream = java.io.ByteArrayOutputStream()
            val formatName = "jpg" // Fallback to jpg for thumbnails implies consistency, or detect from bytes
            javax.imageio.ImageIO.write(resizedImage, formatName, outputStream)
            
            return outputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            return bytes // Fallback to original if resize fails
        }
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
