package com.psmo

import com.psmo.plugins.*
import com.psmo.routes.authRoutes
import com.psmo.security.JwtConfig
import com.psmo.service.AuthService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val jwtConfig = JwtConfig(this)
    val authService = AuthService(jwtConfig)

    configureDatabase()
    configureHTTP()
    configureSerialization()
    configureMonitoring()
    
    install(Authentication) {
        jwtConfig.configureAuth(this)
    }
    
    routing {
        authRoutes(authService)
    }
    
    configureRouting()
}
