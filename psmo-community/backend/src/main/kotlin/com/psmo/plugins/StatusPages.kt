package com.psmo.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

/**
 * 서버에서 에러가 났을 때, 사용자에게 예쁜 에러 메시지를 보여주기 위한 설정입니다.
 *
 * 만약 코딩 실수로 에러가 터지면(Exception), 서버가 그냥 멈추거나 이상한 화면을 보여줄 수 있는데,
 * 이 플러그인이 중간에서 에러를 가로채서 "잠시 문제가 생겼습니다"라고 친절하게 알려줍니다.
 */
fun Application.configureStatusPages() {
    install(StatusPages) {
        // 알 수 없는 모든 에러(Throwable)가 발생하면 여기서 잡습니다. -> 500 에러 반환
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, mapOf("status" to "error", "message" to (cause.message ?: "Unknown error")))
            call.application.environment.log.error("Unhandled exception", cause)
        }
        
        exception<IllegalArgumentException> { call, cause ->
             call.respond(HttpStatusCode.BadRequest, mapOf("status" to "error", "message" to (cause.message ?: "Invalid argument")))
        }
    }
}
