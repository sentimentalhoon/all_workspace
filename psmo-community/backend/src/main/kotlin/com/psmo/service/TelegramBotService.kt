package com.psmo.service

import com.psmo.database.RedisConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.jackson.*
import io.ktor.server.config.*
import io.ktor.server.config.tryGetString
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class TelegramBotService(
    private val config: ApplicationConfig
) {
    private val log = LoggerFactory.getLogger(TelegramBotService::class.java)
    private val botToken: String = config.tryGetString("telegram.botToken")
        ?: System.getenv("TELEGRAM_BOT_TOKEN")
        ?: throw IllegalStateException("Telegram bot token is not configured.")
    private val jedisPool by lazy { RedisConfig.createJedisPool(config) }
    private val mapper = jacksonObjectMapper()

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson()
        }
    }

    private var lastUpdateId = 0L
    private val pollingScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun startPolling() {
        pollingScope.launch {
            log.info("Starting Telegram Bot Polling...")
            while (isActive) {
                try {
                    pollUpdates()
                } catch (e: Exception) {
                    log.error("Error polling Telegram updates", e)
                    delay(5000) // Backoff on error
                }
            }
        }
    }

    fun stopPolling() {
        pollingScope.cancel()
        client.close()
    }

    private suspend fun pollUpdates() {
        val response = client.get("https://api.telegram.org/bot$botToken/getUpdates") {
            parameter("offset", lastUpdateId + 1)
            parameter("timeout", 30) // Long polling
            parameter("allowed_updates", listOf("message"))
        }

        if (response.status.value == 200) {
            val bodyString = response.bodyAsText()
            val root = mapper.readTree(bodyString)
            if (root["ok"].asBoolean()) {
                val result = root["result"]
                if (result.isArray) {
                    for (update in result) {
                        processUpdate(update)
                        lastUpdateId = update["update_id"].asLong()
                    }
                }
            }
        }
    }

    private suspend fun processUpdate(update: JsonNode) {
        val message = update["message"] ?: return
        val text = message["text"]?.asText() ?: return
        val from = message["from"] ?: return
        val telegramId = from["id"].asLong()
        val firstName = from["first_name"]?.asText() ?: ""
        val lastName = from["last_name"]?.asText() ?: ""
        val username = from["username"]?.asText() ?: ""
        
        // Command Format: /start login_<UUID>
        if (text.startsWith("/start login_")) {
            val uuid = text.removePrefix("/start login_").trim()
            if (uuid.isNotEmpty()) {
                handleLoginAttempt(uuid, telegramId, firstName, lastName, username, message["chat"]["id"].asLong())
            }
        }
    }

    private suspend fun handleLoginAttempt(
        uuid: String, 
        telegramId: Long, 
        firstName: String, 
        lastName: String, 
        username: String,
        chatId: Long
    ) {
        val redisKey = "qr_auth:$uuid"
        
        jedisPool.resource.use { jedis ->
            // 1. Check if UUID exists (is valid session)
            if (!jedis.exists(redisKey)) {
                sendMessage(chatId, "⚠️ 만료되었거나 유효하지 않은 로그인 요청입니다. QR 코드를 다시 스캔해주세요.")
                return
            }
            
            // 2. Update Redis with user info (Status: Verified)
            // Store JSON payload for frontend to claim
            val payload = mapOf(
                "telegramId" to telegramId,
                "firstName" to firstName,
                "lastName" to lastName,
                "username" to username,
                "status" to "verified"
            )
            val jsonPayload = mapper.writeValueAsString(payload)
            
            // Update key value and keep TTL (or extend slightly)
            val ttl = jedis.ttl(redisKey).toInt()
            val finalTtl = if (ttl > 0) ttl.toLong() else 300L
            
            jedis.setex(redisKey, finalTtl, jsonPayload)
            
            log.info("QR Login Verified: $uuid for user $telegramId")
            sendMessage(chatId, "✅ 로그인이 확인되었습니다! 웹 브라우저로 돌아가세요.")
        }
    }
    
    private suspend fun sendMessage(chatId: Long, text: String) {
        try {
            client.post("https://api.telegram.org/bot$botToken/sendMessage") {
                parameter("chat_id", chatId)
                parameter("text", text)
            }
        } catch (e: Exception) {
            log.error("Failed to send message to $chatId", e)
        }
    }

    // --- QR Session Management ---

    fun createSession(): String {
        return jedisPool.resource.use { jedis ->
            val uuid = java.util.UUID.randomUUID().toString()
            val redisKey = "qr_auth:$uuid"
            // Initial state: pending
            val payload = mapOf("status" to "pending")
            val jsonPayload = mapper.writeValueAsString(payload)
            jedis.setex(redisKey, 300L, jsonPayload) // 5 minutes TTL
            uuid
        }
    }

    fun checkSession(uuid: String): Map<String, Any>? {
        return jedisPool.resource.use { jedis ->
            val redisKey = "qr_auth:$uuid"
            val json = jedis.get(redisKey) ?: return@use null
            try {
                mapper.readValue(json, Map::class.java) as Map<String, Any>
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Claims the session. If verified, returns the telegram user info and deletes the session.
     */
    fun claimSession(uuid: String): Map<String, Any>? {
        return jedisPool.resource.use { jedis ->
            val redisKey = "qr_auth:$uuid"
            val json = jedis.get(redisKey) ?: return@use null
            val data = mapper.readValue(json, Map::class.java) as Map<String, Any>
            
            if (data["status"] == "verified") {
                jedis.del(redisKey) // Consume the session (One-time use)
                data
            } else {
                null
            }
        }
    }
}
