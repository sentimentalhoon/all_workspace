package com.psmo.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

/**
 * API 설명서(Swagger UI)를 보여주기 위한 설정입니다.
 *
 * 개발자가 만든 API가 어떤 게 있고, 어떻게 써야 하는지 보여주는 웹 페이지를 만듭니다.
 * 브라우저 주소창에 /swagger 라고 치면 볼 수 있습니다.
 */
fun Application.configureSwagger() {
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }
}
