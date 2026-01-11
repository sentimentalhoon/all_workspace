package com.psmo.service

import io.ktor.server.config.*
import org.slf4j.LoggerFactory

class CloudflareStreamService(
    private val config: ApplicationConfig
) {
    private val log = LoggerFactory.getLogger(CloudflareStreamService::class.java)
    
    // TODO: Add Cloudflare Account ID and API Token to application.conf/env
    // private val accountId = config.tryGetString("cloudflare.stream.accountId")
    // private val apiToken = config.tryGetString("cloudflare.stream.apiToken")

    /**
     * TODO: Implement Presigned URL generation for Direct Creator Uploads.
     * https://developers.cloudflare.com/stream/uploading-videos/direct-creator-uploads/
     */
    fun generateUploadUrl(userId: Long): String {
        log.info("Generating Cloudflare Stream upload URL for user $userId")
        throw NotImplementedError("Cloudflare Stream integration is not yet implemented.")
    }

    /**
     * TODO: Implement Webhook handling to receive notifications when video is ready.
     * https://developers.cloudflare.com/stream/webhooks/
     */
    fun handleWebhook(payload: Map<String, Any>) {
        log.info("Received Cloudflare Stream webhook: $payload")
    }

    /**
     * TODO: Implement video trimming logic (if using copy/cut API).
     */
    fun trimVideo(videoId: String, startTime: Double, endTime: Double) {
        log.info("Trimming video $videoId from $startTime to $endTime")
    }
}
