package com.psmo.service

import com.psmo.model.User
import io.ktor.http.Parameters
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.tryGetString
import java.security.MessageDigest
import java.time.Instant
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class TelegramAuthService(
    private val config: ApplicationConfig,
    private val userService: UserService,
    private val jwtService: JwtService
) {
    private val botToken: String = config.tryGetString("telegram.botToken")
        ?: System.getenv("TELEGRAM_BOT_TOKEN")
        ?: throw IllegalStateException("Telegram bot token is not configured. Set telegram.botToken or TELEGRAM_BOT_TOKEN.")

    private val toleranceSeconds: Long =
        config.tryGetString("telegram.authToleranceSeconds")?.toLongOrNull() ?: 86_400L

    fun authenticate(parameters: Parameters): TelegramAuthResponse {
        val data = parameters.entries()
            .associate { (key, values) -> key to values.firstOrNull().orEmpty() }

        val hash = data["hash"] ?: throw TelegramAuthException("hash is missing")
        val dataCheckString = data.entries
            .filter { it.key != "hash" }
            .sortedBy { it.key }
            .joinToString("\n") { "${it.key}=${it.value}" }

        val expectedHash = calculateHash(dataCheckString)
        if (!expectedHash.equals(hash, ignoreCase = true)) {
            throw TelegramAuthException("Invalid Telegram signature", isUnauthorized = true)
        }

        val authDate = data["auth_date"]?.toLongOrNull()
            ?: throw TelegramAuthException("auth_date is missing")
        val now = Instant.now().epochSecond
        if (now - authDate > toleranceSeconds) {
            throw TelegramAuthException("Telegram payload expired", isUnauthorized = true)
        }

        val userId = data["id"]?.toLongOrNull()
            ?: throw TelegramAuthException("id is missing")

        val userRecord = userService.upsertTelegramUser(
            telegramId = userId,
            firstName = data["first_name"],
            lastName = data["last_name"],
            username = data["username"],
            photoUrl = data["photo_url"]
        )
        val token = jwtService.generateAccessToken(userRecord)

        return TelegramAuthResponse(
            user = userRecord.toResponse(),
            token = token,
            authDate = authDate,
            verifiedAt = now
        )
    }

    private fun calculateHash(dataCheckString: String): String {
        val secretKey = MessageDigest.getInstance("SHA-256").digest(botToken.toByteArray())
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secretKey, "HmacSHA256"))
        val hmac = mac.doFinal(dataCheckString.toByteArray())
        return hmac.joinToString("") { "%02x".format(it) }
    }

    private fun User.toResponse(): UserResponse = UserResponse(
        id = id,
        telegramId = telegramId,
        displayName = displayName,
        username = username,
        photoUrl = photoUrl
    )
}

data class TelegramAuthResponse(
    val status: String = "success",
    val user: UserResponse,
    val token: TokenResponse,
    val authDate: Long,
    val verifiedAt: Long
)

data class UserResponse(
    val id: Long,
    val telegramId: Long,
    val displayName: String?,
    val username: String?,
    val photoUrl: String?
)

class TelegramAuthException(
    override val message: String,
    val isUnauthorized: Boolean = false
) : RuntimeException(message)
