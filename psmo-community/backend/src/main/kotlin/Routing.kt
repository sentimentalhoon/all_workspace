package com.psmo

import com.psmo.service.TestService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val testService = TestService()
    
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
    }
}
