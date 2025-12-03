package com.psmo

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.psmo.config.ProfiledConfigLoader
import com.psmo.database.DatabaseConfig
import io.ktor.http.*
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.http.auth.parseAuthorizationHeader
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.ApplicationConfigurationException
import io.ktor.server.config.tryGetString
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*

/**
 * PSMO 커뮤니티 백엔드 서버의 진입점.
 * Ktor EngineMain 을 그대로 사용하지만, 배포 환경에 따라 JVM 옵션을 다르게 주입할 수 있으므로
 * 별도 래핑 함수를 유지한다.
 */
fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

/**
 * 애플리케이션 전역 모듈을 구성한다.
 * - profile 별 application-<profile>.yaml 을 로드
 * - CORS/ContentNegotiation/JWT 인증 플러그인 설치
 * - 실제 라우팅은 configureRouting 으로 위임
 */
fun Application.module() {
    val (activeProfile, activeConfig) = ProfiledConfigLoader.load(environment)
    environment.log.info("Active KTOR_PROFILE: $activeProfile (application-$activeProfile.yaml)")

    // profile 이 다를 때마다 allowedOrigins 형식이 list/string 으로 혼용되어 있어 보정한다.
    val allowedOrigins = activeConfig.propertyOrNull("cors.allowedOrigins")?.let { value ->
        try {
            value.getList()
        } catch (_: ApplicationConfigurationException) {
            value.getString()
                .split(',')
                .mapNotNull { it.trim().takeIf(String::isNotEmpty) }
        }
    } ?: listOf("http://localhost:5173", "http://localhost:5174")

    // TODO: 운영 환경에서 secret 을 반드시 Secret Manager/KeyVault 로 대체
    val jwtSecret = activeConfig.tryGetString("jwt.secret")
        ?: System.getenv("JWT_SECRET")
        ?: error("JWT secret is not configured.")
    val jwtIssuer = activeConfig.tryGetString("jwt.issuer") ?: "psmo-community"
    val jwtAudience = activeConfig.tryGetString("jwt.audience") ?: "psmo-app"
    val jwtRealm = activeConfig.tryGetString("jwt.realm") ?: "PSMO Community"
    val jwtAlgorithm = Algorithm.HMAC256(jwtSecret)

    // 서버 부팅 시점에 Flyway 마이그레이션이 적용되도록 명시적으로 초기화한다.
    DatabaseConfig.connectToDatabase(activeConfig)

    install(ContentNegotiation) {
        jackson()
    }

    install(WebSockets)

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
            authHeader { call ->
                call.request.headers[HttpHeaders.Authorization]
                    ?.let(::parseAuthorizationHeader)
                    ?: call.request.queryParameters["token"]
                        ?.takeIf { it.isNotBlank() }
                        ?.let { HttpAuthHeader.Single("Bearer", it) }
            }
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
