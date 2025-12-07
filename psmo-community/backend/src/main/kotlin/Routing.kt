package com.psmo

import com.psmo.model.dto.ProfileResponse
import com.psmo.model.dto.toResponse
import com.psmo.service.ChatRoomManager
import com.psmo.service.ChatService
import com.psmo.service.JwtService
import com.psmo.service.RefreshTokenService
import com.psmo.service.SnailRaceService
import com.psmo.service.TestService
import com.psmo.service.UserService
import com.psmo.service.TelegramAuthService
import com.psmo.service.TelegramAuthException
import com.psmo.service.BetValidationException
import com.psmo.service.CooldownException
import com.psmo.service.RaceAlreadyReportedException
import com.psmo.service.RaceExpiredException
import com.psmo.service.RaceForbiddenException
import com.psmo.service.RaceInternalException
import com.psmo.service.RaceNotFoundException
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
import com.psmo.model.dto.SnailRaceStartRequestDto
import com.psmo.model.dto.SnailRaceResultPayloadDto

/**
 * API 라우팅 진입점. 의존성 간접 생성이 필요한 서비스들을 여기에서 조립한다.
 * NOTE: Koin/Hilt 등 DI 를 도입하면 이 부분을 대체할 수 있다.
 */
fun Application.configureRouting(config: ApplicationConfig) {
    val testService = TestService(config)
    val userService = UserService(config)
    val jwtService = JwtService(config)
    val refreshTokenService = RefreshTokenService()
    val telegramAuthService = TelegramAuthService(config, userService, jwtService, refreshTokenService)
    val snailRaceService = SnailRaceService(config, userService)
    val chatService = ChatService(config)
    val chatSubscriptionScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val chatBroadcastScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val chatRoomManager = ChatRoomManager(chatService, userService, chatSubscriptionScope, chatBroadcastScope)

    monitor.subscribe(ApplicationStopped) {
        chatRoomManager.shutdown()
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

        route("/api/auth") {
            post("/telegram") {
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

            post("/refresh") {
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

            post("/logout") {
                val refreshToken = call.request.cookies["refresh_token"]
                if (refreshToken != null) {
                    refreshTokenService.revokeRefreshToken(refreshToken)
                }
                call.response.cookies.append(name = "refresh_token", value = "", maxAge = 0, expires = GMTDate.START, path = "/api/auth")
                call.respond(HttpStatusCode.OK, mapOf("status" to "success"))
            }
        }

        authenticate("auth-jwt") {
            get("/api/me") {
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

            route("/api/games/snail") {
                post("/races") {
                    val principal = call.principal<JWTPrincipal>() ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("status" to "error", "message" to "Authentication required")
                    )
                    val userId = principal.subject?.toLongOrNull() ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("status" to "error", "message" to "Invalid token payload")
                    )
                    val user = userService.getUserById(userId) ?: return@post call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("status" to "error", "message" to "User not found")
                    )

                    val traceId = call.request.headers["X-Request-ID"] ?: UUID.randomUUID().toString()
                    val remote = call.request.local.remoteHost
                    MDC.put("traceId", traceId)
                    MDC.put("remote", remote)
                    try {
                        this@configureRouting.environment.log.info("snail-race start request userId={}", user.id)

                        val payload = call.receive<SnailRaceStartRequestDto>()
                        val response = snailRaceService.startRace(user, payload)
                        call.respond(HttpStatusCode.OK, response)
                    } catch (ex: BetValidationException) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("status" to "error", "code" to ex.code, "message" to ex.message))
                    } catch (ex: CooldownException) {
                        call.respond(HttpStatusCode.TooManyRequests, mapOf("status" to "error", "code" to "COOLDOWN", "remainingSeconds" to ex.remainingSeconds))
                    } catch (ex: Exception) {
                        this@configureRouting.environment.log.error("startRace failed", ex)
                        call.respond(HttpStatusCode.InternalServerError, mapOf("status" to "error", "message" to "Internal server error"))
                    } finally {
                        MDC.clear()
                    }
                }

                post("/races/{raceId}/complete") {
                    val principal = call.principal<JWTPrincipal>() ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("status" to "error", "message" to "Authentication required")
                    )
                    val userId = principal.subject?.toLongOrNull() ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("status" to "error", "message" to "Invalid token payload")
                    )
                    val user = userService.getUserById(userId) ?: return@post call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("status" to "error", "message" to "User not found")
                    )

                    val raceId = call.parameters["raceId"]
                        ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("status" to "error", "message" to "raceId missing"))
                    val payload = call.receive<SnailRaceResultPayloadDto>()

                    val traceId = call.request.headers["X-Request-ID"] ?: UUID.randomUUID().toString()
                    val remote = call.request.local.remoteHost
                    MDC.put("traceId", traceId)
                    MDC.put("remote", remote)
                    try {
                        this@configureRouting.environment.log.info(
                            "snail-race complete request userId={} raceId={}",
                            user.id,
                            raceId
                        )

                        val response = snailRaceService.completeRace(user, raceId, payload)
                        call.respond(HttpStatusCode.OK, response)
                    } catch (ex: RaceNotFoundException) {
                        call.respond(HttpStatusCode.NotFound, mapOf("status" to "error", "code" to "RACE_NOT_FOUND"))
                    } catch (ex: RaceForbiddenException) {
                        call.respond(HttpStatusCode.Forbidden, mapOf("status" to "error", "code" to "RACE_FORBIDDEN", "message" to "Race not owned"))
                    } catch (ex: RaceAlreadyReportedException) {
                        call.respond(HttpStatusCode.Conflict, mapOf("status" to "error", "code" to "ALREADY_REPORTED"))
                    } catch (ex: RaceExpiredException) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("status" to "error", "code" to "RACE_EXPIRED"))
                    } catch (ex: BetValidationException) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("status" to "error", "code" to ex.code, "message" to ex.message))
                    } catch (ex: RaceInternalException) {
                        this@configureRouting.environment.log.error("completeRace internal", ex)
                        call.respond(HttpStatusCode.InternalServerError, mapOf("status" to "error", "message" to ex.message))
                    } catch (ex: Exception) {
                        this@configureRouting.environment.log.error("completeRace failed", ex)
                        call.respond(HttpStatusCode.InternalServerError, mapOf("status" to "error", "message" to "Internal server error"))
                    } finally {
                        MDC.clear()
                    }
                }
            }

            webSocket("/ws/chat") {
                val principal = call.principal<JWTPrincipal>()
                if (principal == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Authentication required"))
                    return@webSocket
                }

                val userId = principal.subject?.toLongOrNull()
                if (userId == null) {
                    close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Invalid token"))
                    return@webSocket
                }

                val user = userService.getUserById(userId)
                if (user == null) {
                    close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "User not found"))
                    return@webSocket
                }

                chatRoomManager.handleSession(this, user)
            }
        }
    }
}
