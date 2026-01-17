package com.psmo

import com.psmo.model.dto.ProfileResponse
import com.psmo.model.dto.toResponse
import com.psmo.service.*
import com.psmo.controller.productRoutes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.http.*
import io.ktor.util.date.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import java.util.UUID
import org.slf4j.MDC

import org.koin.ktor.ext.inject
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import com.psmo.resources.*

/**
 * API 라우팅 진입점. 의존성 간접 생성이 필요한 서비스들을 여기에서 조립한다.
 * NOTE: Koin DI 가 도입되어 의존성을 주입받는다.
 */
/**
 * API 라우팅 진입점입니다. (길 안내소)
 * "어떤 주소(/login, /market)로 요청이 오면, 어떤 함수를 실행해라"라고 정해주는 곳입니다.
 *
 * 여기서 Koin(inject)을 사용해서 필요한 서비스(UserService 등)를 가져와서 연결해줍니다.
 */
fun Application.configureRouting(config: ApplicationConfig) {
    val testService by inject<TestService>()
    val userService by inject<UserService>()
    val jwtService by inject<JwtService>()
    val refreshTokenService by inject<RefreshTokenService>()
    val telegramAuthService by inject<TelegramAuthService>()
    val telegramBotService by inject<TelegramBotService>()
    val productService by inject<ProductService>()

    // 서버가 켜질 때(ApplicationStarted) 텔레그램 봇도 같이 일을 시작하라고 시킵니다.
    monitor.subscribe(ApplicationStarted) {
        launch { telegramBotService.startPolling() } // Launch in a coroutine
    }
    
    // 서버가 꺼질 때(ApplicationStopped) 텔레그램 봇도 퇴근시킵니다.
    monitor.subscribe(ApplicationStopped) {
        telegramBotService.stopPolling()
    }

    routing {
        get("/") {
            call.respondText("PSMO Community Backend is running!")
        }

        get("/api/health") {
            // 경량 상태 점검 엔드포인트. 추후 DB/Redis 상태도 함께 보고할 수 있음.
            call.respond(
                mapOf(
                    "status" to "UP",
                    "service" to "PSMO Community Backend"
                )
            )
        }

        route("/api/test") {
            // 운영 배포 시 노출 여부를 다시 검토할 필요 있음.
            get("/postgres") {
                call.respond(testService.testPostgreSQL())
            }

            get("/redis") {
                call.respond(testService.testRedis())
            }

            get("/minio") {
                call.respond(testService.testMinIO())
            }

            get("/all") {
                call.respond(testService.testAll())
            }
        }

        // Market 관련 API들은 다른 파일(ProductController)에 따로 정리해두었습니다.
        // 여기서 불러옵니다.
        // Market Routes
        productRoutes(productService)

        // 텔레그램 로그인 요청을 처리합니다.
        // 프론트엔드에서 받은 로그인 정보(Telegram Widget 데이터)를 검증하고, 우리가 만든 '출입증(JWT 토큰)'을 발급해줍니다.
        // Type-Safe Routing: URL 주소를 문자열("/api/auth/telegram") 대신 객체(Api.Auth.Telegram)로 안전하게 다룹니다.
        post<Api.Auth.Telegram> {
            val params = call.receiveParameters()
            try {
                val (authResponse, refreshToken) = telegramAuthService.authenticate(params)
                
                // Refresh Token 을 HttpOnly Cookie 로 설정
                call.response.cookies.append(
                    name = "refresh_token",
                    value = refreshToken,
                    httpOnly = true,
                    secure = true, // HTTPS 환경에서만 전송 (개발환경에서는 false 로 해야할 수도 있음)
                    path = "/api/auth", // Auth 관련 경로에서만 쿠키 전송
                    maxAge = jwtService.getRefreshExpirationSeconds(),
                    extensions = mapOf("SameSite" to "Lax")
                )
                
                call.respond(HttpStatusCode.OK, authResponse)
            } catch (ex: TelegramAuthException) {
                val status = if (ex.isUnauthorized) HttpStatusCode.Unauthorized else HttpStatusCode.BadRequest
                call.respond(status, mapOf("status" to "error", "message" to ex.message))
            } catch (ex: Exception) {
                // TODO: 실패 지표/알람을 추가하여 Telegram 로그인 실패를 추적
                this@configureRouting.environment.log.error("Telegram auth failed", ex)
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("status" to "error", "message" to "Internal server error")
                )
            }
        }

        post<Api.Auth.Refresh> {
            val refreshToken = call.request.cookies["refresh_token"]
            if (refreshToken == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "error", "message" to "Refresh token missing"))
                return@post
            }

            try {
                val result = refreshTokenService.rotateRefreshToken(refreshToken, jwtService.getRefreshExpirationSeconds())
                if (result == null) {
                    // 유효하지 않거나 만료된 토큰 -> 쿠키 삭제
                    call.response.cookies.append(name = "refresh_token", value = "", maxAge = 0, expires = GMTDate.START, path = "/api/auth")
                    call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "error", "message" to "Invalid refresh token"))
                    return@post
                }

                val (newRefreshToken, user) = result

                // 새 Refresh Token 쿠키 설정
                call.response.cookies.append(
                    name = "refresh_token",
                    value = newRefreshToken,
                    httpOnly = true,
                    secure = true,
                    path = "/api/auth",
                    maxAge = jwtService.getRefreshExpirationSeconds(),
                    extensions = mapOf("SameSite" to "Lax")
                )
                
                val newAccessToken = jwtService.generateAccessToken(user)
                call.respond(HttpStatusCode.OK, mapOf("token" to newAccessToken))

            } catch (ex: Exception) {
                this@configureRouting.environment.log.error("Token refresh failed", ex)
                call.respond(HttpStatusCode.InternalServerError, mapOf("status" to "error", "message" to "Internal server error"))
            }
        }

        post<Api.Auth.Logout> {
            val refreshToken = call.request.cookies["refresh_token"]
            if (refreshToken != null) {
                refreshTokenService.revokeRefreshToken(refreshToken)
            }
            call.response.cookies.append(name = "refresh_token", value = "", maxAge = 0, expires = GMTDate.START, path = "/api/auth")
            call.respond(HttpStatusCode.OK, mapOf("status" to "success"))
        }

        // --- QR Code Login Routes ---
        
        post<Qr.Init> {
            val uuid = telegramBotService.createSession()
            val botName = telegramBotService.getBotUsername() 
            // Return deep link using global bot name
            val deepLink = "https://t.me/$botName?start=login_$uuid"
            
            call.respond(mapOf(
                "uuid" to uuid,
                "deepLink" to deepLink,
                "expiresIn" to 300
            ))
        }

        get<Qr.Check> {
            val uuid = call.request.queryParameters["uuid"] 
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("status" to "error", "message" to "uuid missing"))
            
            val statusMap = telegramBotService.checkSession(uuid)
            if (statusMap == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("status" to "expired"))
            } else {
                call.respond(statusMap)
            }
        }

        post<Qr.Claim> {
            val params = call.receive<Map<String, String>>()
            val uuid = params["uuid"] ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("status" to "error", "message" to "uuid missing"))
            
            val claimData = telegramBotService.claimSession(uuid)
            if (claimData != null) {
                // Login Success!
                // Extract user data from claimData (which was stored by Bot)
                val telegramId = (claimData["telegramId"] as Number).toLong()
                val firstName = claimData["firstName"] as? String
                val lastName = claimData["lastName"] as? String
                val username = claimData["username"] as? String
                val photoUrl = claimData["photoUrl"] as? String
                
                // Reuse existing upsert logic
                val userRecord = userService.upsertTelegramUser(
                    telegramId = telegramId,
                    firstName = firstName,
                    lastName = lastName,
                    username = username,
                    photoUrl = photoUrl
                )
                
                val accessToken = jwtService.generateAccessToken(userRecord)
                val refreshToken = refreshTokenService.createRefreshToken(userRecord, jwtService.getRefreshExpirationSeconds())
                
                // Cookie Settings
                call.response.cookies.append(
                    name = "refresh_token",
                    value = refreshToken,
                    httpOnly = true,
                    secure = true, 
                    path = "/api/auth",
                    maxAge = jwtService.getRefreshExpirationSeconds(),
                    extensions = mapOf("SameSite" to "Lax")
                )
                
                call.respond(TelegramAuthResponse(
                    user = userRecord.toResponse(),
                    token = accessToken,
                    authDate = System.currentTimeMillis() / 1000,
                    verifiedAt = System.currentTimeMillis() / 1000
                ))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "error", "message" to "Session not verified or expired"))
            }
        }

        authenticate("auth-jwt") {
            get<Api.Me> {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("status" to "error", "message" to "Authentication required")
                    )

                val userId = principal.subject?.toLongOrNull()
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("status" to "error", "message" to "Invalid token payload")
                    )

                val user = userService.getUserById(userId)
                    ?: return@get call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("status" to "error", "message" to "User not found")
                    )

                call.respond(ProfileResponse(user = user.toResponse()))
            }
        }
    }
}
