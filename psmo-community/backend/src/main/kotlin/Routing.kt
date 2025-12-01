package com.psmo

import com.psmo.model.dto.ProfileResponse
import com.psmo.model.dto.toResponse
import com.psmo.service.JwtService
import com.psmo.service.TelegramAuthException
import com.psmo.service.TelegramAuthService
import com.psmo.service.TestService
import com.psmo.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(config: ApplicationConfig) {
    val testService = TestService(config)
    val userService = UserService(config)
    val jwtService = JwtService(config)
    val telegramAuthService = TelegramAuthService(config, userService, jwtService)

    routing {
        get("/") {
            call.respondText("PSMO Community Backend is running!")
        }

        get("/api/health") {
            call.respond(
                mapOf(
                    "status" to "UP",
                    "service" to "PSMO Community Backend"
                )
            )
        }

        route("/api/test") {
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
                    val result = telegramAuthService.authenticate(params)
                    call.respond(HttpStatusCode.OK, result)
                } catch (ex: TelegramAuthException) {
                    val status = if (ex.isUnauthorized) HttpStatusCode.Unauthorized else HttpStatusCode.BadRequest
                    call.respond(status, mapOf("status" to "error", "message" to ex.message))
                } catch (ex: Exception) {
                    this@configureRouting.environment.log.error("Telegram auth failed", ex)
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("status" to "error", "message" to "Internal server error")
                    )
                }
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
        }
    }
}
