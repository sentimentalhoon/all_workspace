package com.psmo

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.psmo.config.ProfiledConfigLoader
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.ApplicationConfigurationException
import io.ktor.server.config.tryGetString
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val (activeProfile, activeConfig) = ProfiledConfigLoader.load(environment)
    environment.log.info("Active KTOR_PROFILE: $activeProfile (application-$activeProfile.yaml)")

    val allowedOrigins = activeConfig.propertyOrNull("cors.allowedOrigins")?.let { value ->
        try {
            value.getList()
        } catch (_: ApplicationConfigurationException) {
            value.getString()
                .split(',')
                .mapNotNull { it.trim().takeIf(String::isNotEmpty) }
        }
    } ?: listOf("http://localhost:5173", "http://localhost:5174")

    val jwtSecret = activeConfig.tryGetString("jwt.secret")
        ?: System.getenv("JWT_SECRET")
        ?: error("JWT secret is not configured.")
    val jwtIssuer = activeConfig.tryGetString("jwt.issuer") ?: "psmo-community"
    val jwtAudience = activeConfig.tryGetString("jwt.audience") ?: "psmo-app"
    val jwtRealm = activeConfig.tryGetString("jwt.realm") ?: "PSMO Community"
    val jwtAlgorithm = Algorithm.HMAC256(jwtSecret)

    install(ContentNegotiation) {
        jackson()
    }

    install(CORS) {
        allowedOrigins.forEach { origin ->
            allowHost(
                origin.removePrefix("http://").removePrefix("https://"),
                schemes = listOf("http", "https")
            )
        }
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowCredentials = true
    }

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT.require(jwtAlgorithm)
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("status" to "error", "message" to "Invalid or expired token")
                )
            }
        }
    }

    configureRouting(activeConfig)
}
