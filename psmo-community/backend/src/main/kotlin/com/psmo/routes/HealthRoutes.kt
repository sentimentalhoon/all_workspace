package com.psmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class HealthResponse(
    val status: String,
    val timestamp: String,
    val service: String
)

fun Route.healthRoutes() {
    route("/api") {
        route("/health") {
            get {
                call.respond(
                    HttpStatusCode.OK,
                    HealthResponse(
                        status = "UP",
                        timestamp = LocalDateTime.now().toString(),
                        service = "psmo-community-backend"
                    )
                )
            }
            head {
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
