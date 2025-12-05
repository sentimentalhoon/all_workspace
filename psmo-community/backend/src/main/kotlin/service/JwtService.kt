package com.psmo.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.psmo.model.User
import com.psmo.model.dto.TokenResponse
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.tryGetString
import java.util.Date

/**
 * JWT 토큰 생성 책임을 분리한 서비스.
 * HMAC256 대칭키 기반이며, 환경변수/설정파일에서 키를 찾는다.
 */
class JwtService(
    config: ApplicationConfig
) {
    private val secret = config.tryGetString("jwt.secret")
        ?: System.getenv("JWT_SECRET")
        ?: throw IllegalStateException("JWT secret is not configured.")
    private val issuer = config.tryGetString("jwt.issuer") ?: "psmo-community"
    private val audience = config.tryGetString("jwt.audience") ?: "psmo-app"
    private val expirationMs = config.tryGetString("jwt.expiration")?.toLongOrNull() ?: 1_800_000L // 기본 30분
    private val refreshExpirationMs = config.tryGetString("jwt.refreshExpiration")?.toLongOrNull() ?: 1_209_600_000L // 기본 14일

    private val algorithm = Algorithm.HMAC256(secret)

    fun getRefreshExpirationSeconds(): Long = refreshExpirationMs / 1000

    /**
     * 사용자 정보 기반 access token 을 발급한다.
     */
    fun generateAccessToken(user: User): TokenResponse {
        val expiresAt = Date(System.currentTimeMillis() + expirationMs)
        val token = JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withSubject(user.id.toString())
            .withClaim("telegram_id", user.telegramId)
            .withClaim("username", user.username)
            .withClaim("role", user.role.name)
            .withClaim("activity_level", user.activityLevel)
            .withExpiresAt(expiresAt)
            .sign(algorithm)

        return TokenResponse(
            accessToken = token,
            expiresAt = expiresAt.toInstant().epochSecond
        )
    }
}
