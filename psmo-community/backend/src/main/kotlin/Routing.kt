package com.psmo

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
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
    }
}
