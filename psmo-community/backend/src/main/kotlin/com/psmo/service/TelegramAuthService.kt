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
 * 텔레그램 로그인 위젯이 보내준 데이터가 진짜인지 확인하고, 우리 사이트 전용 토큰을 발급해주는 역할을 합니다.
 *
 * TODO: 나중에 애플, 구글 로그인도 추가되면 이 구조를 좀 더 일반화해야 합니다.
 */
class TelegramAuthService(
    private val config: ApplicationConfig,
    private val userService: UserService,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService
) {
    private val botToken: String = config.tryGetString("telegram.botToken")
        ?: System.getenv("TELEGRAM_BOT_TOKEN")
        ?: throw IllegalStateException("Telegram bot token is not configured. Set telegram.botToken or TELEGRAM_BOT_TOKEN.")

    private val toleranceSeconds: Long =
        config.tryGetString("telegram.authToleranceSeconds")?.toLongOrNull() ?: 86_400L

    /**
     * 텔레그램에서 온 데이터를 검증하고, 로그인 성공 시 토큰 2개를 줍니다.
     * 1. Access Token: 평소에 api 호출할 때 쓰는 짧은 출입증
     * 2. Refresh Token: Access Token이 만료되면 재발급받을 때 쓰는 긴 출입증 (보안을 위해 쿠키에 담음)
     */
    fun authenticate(parameters: Parameters): Pair<TelegramAuthResponse, String> {
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
        
        val accessToken = jwtService.generateAccessToken(userRecord)
        val refreshToken = refreshTokenService.createRefreshToken(userRecord, jwtService.getRefreshExpirationSeconds())

        return Pair(
            TelegramAuthResponse(
                user = userRecord.toResponse(),
                token = accessToken,
                authDate = authDate,
                verifiedAt = now
            ),
            refreshToken
        )
    }

    /**
     * 텔레그램이 정해준 규칙대로 암호(Hash)를 계산해봅니다.
     * 우리가 가진 비밀키(Bot Token)로 계산한 값과, 텔레그램이 보내준 값이 일치하면 "진짜"이고,
     * 다르면 "가짜(위조된 요청)"입니다.
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
