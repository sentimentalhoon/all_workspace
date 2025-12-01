package com.psmo

import com.psmo.config.ProfiledConfigLoader
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.config.tryGetStringList
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val (activeProfile, activeConfig) = ProfiledConfigLoader.load(environment)
    environment.log.info("Active KTOR_PROFILE: $activeProfile (application-$activeProfile.yaml)")

    val allowedOrigins = activeConfig.tryGetStringList("cors.allowedOrigins")
        ?: listOf("http://localhost:5173", "http://localhost:5174")

    install(ContentNegotiation) {
        jackson()
    }

    install(CORS) {
        allowedOrigins.forEach { origin ->
            allowHost(
                origin.removePrefix("http://").removePrefix("https://"),
                schemes = listOf("http", "https")
            )
        }
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowCredentials = true
    }

    configureRouting(activeConfig)
}
