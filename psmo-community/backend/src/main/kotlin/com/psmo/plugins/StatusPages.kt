package com.psmo.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, mapOf("status" to "error", "message" to (cause.message ?: "Unknown error")))
            call.application.environment.log.error("Unhandled exception", cause)
        }
        
        exception<IllegalArgumentException> { call, cause ->
             call.respond(HttpStatusCode.BadRequest, mapOf("status" to "error", "message" to (cause.message ?: "Invalid argument")))
        }
    }
}
