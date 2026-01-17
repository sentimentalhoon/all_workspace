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
import com.typesafe.config.ConfigException
import io.ktor.server.config.ApplicationConfigurationException
import io.ktor.server.config.tryGetString
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import io.ktor.server.resources.*
import com.psmo.plugins.configureSwagger
import com.psmo.plugins.configureDI
import com.psmo.plugins.configureStatusPages

/**
 * PSMO 커뮤니티 백엔드 서버의 진입점(Entry Point)입니다.
 *
 * 이 함수는 프로그램이 시작될 때 가장 먼저 실행되는 곳입니다.
 * 마치 학교 정문과 같아서, 모든 요청은 이곳을 통해 들어오게 됩니다.
 *
 * Ktor 프레임워크의 EngineMain을 사용하여 서버를 켭니다.
 * 배포 환경(개발 서버, 운영 서버 등)에 따라 설정을 다르게 할 수 있도록 도와줍니다.
 */
fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

/**
 * 애플리케이션의 전체적인 설정을 담당하는 모듈 함수입니다.
 *
 * 여기서 하는 일:
 * 1. 설정 로드: 'dev'(개발) 또는 'prod'(운영) 같은 환경 설정을 읽어옵니다.
 * 2. 플러그인 설치:
 *    - CORS: 다른 웹사이트(프론트엔드)에서 이 서버로 요청을 보낼 수 있게 허락해줍니다.
 *    - ContentNegotiation: 데이터를 JSON 형식(자바스크립트 객체 모양)으로 주고받을 수 있게 합니다.
 *    - Authentication (JWT): 로그인한 사용자만 사용할 수 있는 기능을 위해 '출입증(토큰)'을 검사하는 기능을 켭니다.
 * 3. 기능 연결:
 *    - configureRouting: 실제 api 주소들(/login, /market 등)을 연결합니다.
 *    - configureDI: 필요한 부품들(Service, Repository)을 조립합니다.
 */
fun Application.module() {
    val (activeProfile, activeConfig) = ProfiledConfigLoader.load(environment)
    environment.log.info("Active KTOR_PROFILE/KTOR_ENV: $activeProfile (application.conf)")

    configureDI(activeConfig)
    configureStatusPages()

    // profile 이 다를 때마다 allowedOrigins 형식이 list/string 으로 혼용되어 있어 보정한다.
    val allowedOrigins = activeConfig.propertyOrNull("cors.allowedOrigins")?.let { value ->
        try {
            value.getList()
        } catch (_: ApplicationConfigurationException) {
            value.getString()
                .split(',')
                .mapNotNull { it.trim().takeIf(String::isNotEmpty) }
        } catch (_: ConfigException.WrongType) {
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
    install(Resources)

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
    configureSwagger()
}
