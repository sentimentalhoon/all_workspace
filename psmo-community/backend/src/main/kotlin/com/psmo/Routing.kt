package com.psmo

import com.psmo.model.dto.ProfileResponse
import com.psmo.model.dto.toResponse
import com.psmo.model.dto.toBlackjackResponse
import com.psmo.service.ChatRoomManager
import com.psmo.service.ChatService
import com.psmo.service.JwtService
import com.psmo.service.RefreshTokenService
import com.psmo.service.SnailRaceService
import com.psmo.service.TestService
import com.psmo.service.UserService
import com.psmo.service.TelegramAuthService
import com.psmo.service.TelegramAuthException
import com.psmo.service.TelegramBotService
import com.psmo.service.TelegramAuthResponse
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

import org.koin.ktor.ext.inject
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import com.psmo.resources.*

/**
 * API 라우팅 진입점. 의존성 간접 생성이 필요한 서비스들을 여기에서 조립한다.
 * NOTE: Koin DI 가 도입되어 의존성을 주입받는다.
 */
fun Application.configureRouting(config: ApplicationConfig) {
    val testService by inject<TestService>()
    val userService by inject<UserService>()
    val jwtService by inject<JwtService>()
    val refreshTokenService by inject<RefreshTokenService>()
    val telegramAuthService by inject<TelegramAuthService>()
    val telegramBotService by inject<TelegramBotService>()
    val snailRaceService by inject<SnailRaceService>()
    val blackjackService by inject<com.psmo.service.BlackjackService>()
    // val chatService by inject<ChatService>() // Unused
    val chatRoomManager by inject<ChatRoomManager>()

    monitor.subscribe(ApplicationStarted) {
        launch { telegramBotService.startPolling() } // Launch in a coroutine
    }
    
    monitor.subscribe(ApplicationStopped) {
        chatRoomManager.shutdown()
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

        // Type-Safe Routing for Auth
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
            
            route("/api/games/blackjack") {
                post("/start") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val user = userService.getUserById(userId) ?: return@post call.respond(HttpStatusCode.NotFound)
                    
                    try {
                        val payload = call.receive<com.psmo.model.dto.BlackjackStartRequestDto>()
                        val game = blackjackService.startGame(user, payload.betAmount)
                        call.respond(game.toBlackjackResponse())
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                    }
                }
                
                post("/{gameId}/hit") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val gameId = call.parameters["gameId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    
                    try {
                        val game = blackjackService.hit(gameId, userId)
                        call.respond(game.toBlackjackResponse())
                    } catch (e: Exception) {
                         call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                    }
                }
                
                post("/{gameId}/stand") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val gameId = call.parameters["gameId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    
                    try {
                        val game = blackjackService.stand(gameId, userId)
                        call.respond(game.toBlackjackResponse())
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
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
