package com.psmo.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.psmo.model.User
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.tryGetString
import java.util.Date

class JwtService(
    config: ApplicationConfig
) {
    private val secret = config.tryGetString("jwt.secret")
        ?: System.getenv("JWT_SECRET")
        ?: throw IllegalStateException("JWT secret is not configured.")
    private val issuer = config.tryGetString("jwt.issuer") ?: "psmo-community"
    private val audience = config.tryGetString("jwt.audience") ?: "psmo-app"
    private val expirationMs = config.tryGetString("jwt.expiration")?.toLongOrNull() ?: 86_400_000L

    private val algorithm = Algorithm.HMAC256(secret)

    fun generateAccessToken(user: User): TokenResponse {
        val expiresAt = Date(System.currentTimeMillis() + expirationMs)
        val token = JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withSubject(user.id.toString())
            .withClaim("telegram_id", user.telegramId)
            .withClaim("username", user.username)
            .withExpiresAt(expiresAt)
            .sign(algorithm)

        return TokenResponse(
            accessToken = token,
            expiresAt = expiresAt.toInstant().epochSecond
        )
    }
}

data class TokenResponse(
    val accessToken: String,
    val expiresAt: Long
)
