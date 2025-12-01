package com.psmo.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*

fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowCredentials = true
        
        // Development and Production hosts
        allowHost("localhost:5173")
        allowHost("localhost:3000")
        allowHost("mycommunity.duckdns.org", schemes = listOf("https"))
    }
    
    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }
}
