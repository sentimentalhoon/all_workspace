package com.psmo.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

class JwtConfig(private val application: Application) {
    private val secret = application.environment.config.property("jwt.secret").getString()
    private val issuer = application.environment.config.property("jwt.issuer").getString()
    private val audience = application.environment.config.property("jwt.audience").getString()
    private val realm = application.environment.config.property("jwt.realm").getString()
    val expiration = application.environment.config.property("jwt.expiration").getString().toLong()
    val refreshExpiration = application.environment.config.property("jwt.refreshExpiration").getString().toLong()

    fun generateToken(email: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withSubject(email)
            .withExpiresAt(Date(System.currentTimeMillis() + expiration))
            .sign(Algorithm.HMAC256(secret))
    }

    fun generateRefreshToken(email: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withSubject(email)
            .withExpiresAt(Date(System.currentTimeMillis() + refreshExpiration))
            .sign(Algorithm.HMAC256(secret))
    }

    fun configureAuth(config: AuthenticationConfig) {
        config.jwt("auth-jwt") {
            realm = this@JwtConfig.realm
            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}

fun ApplicationCall.getUserEmail(): String? {
    return principal<JWTPrincipal>()?.subject
}
