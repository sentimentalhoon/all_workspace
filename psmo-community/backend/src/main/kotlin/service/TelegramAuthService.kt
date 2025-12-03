package com.psmo.service

import com.psmo.model.dto.TokenResponse
import com.psmo.model.dto.UserResponse
import com.psmo.model.dto.toResponse
import io.ktor.http.Parameters
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.tryGetString
import java.security.MessageDigest
import java.time.Instant
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Telegram Login Widget 서명 검증과 사용자 토큰 발급을 담당.
 * TODO: 향후 Apple/Google OAuth 등 멀티 공급자 전략으로 확장하도록 추상화 레이어 필요
 */
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

    /**
     * Telegram 으로부터 전달된 파라미터를 검증하고 사용자 토큰을 반환한다.
     */
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

    /**
     * Telegram 사양에 맞춰 데이터 체크 문자열을 HMAC-SHA256 으로 검증한다.
     */
    private fun calculateHash(dataCheckString: String): String {
        val secretKey = MessageDigest.getInstance("SHA-256").digest(botToken.toByteArray())
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secretKey, "HmacSHA256"))
        val hmac = mac.doFinal(dataCheckString.toByteArray())
        return hmac.joinToString("") { "%02x".format(it) }
    }
}

/**
 * 인증 성공 응답 DTO.
 */
data class TelegramAuthResponse(
    val status: String = "success",
    val user: UserResponse,
    val token: TokenResponse,
    val authDate: Long,
    val verifiedAt: Long
)

/**
 * Telegram 인증 관련 예외.
 */
class TelegramAuthException(
    override val message: String,
    val isUnauthorized: Boolean = false
) : RuntimeException(message)
