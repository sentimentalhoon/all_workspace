package com.psmo.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.psmo.model.User
import com.psmo.model.dto.ChatBroadcastMessage
import com.psmo.model.dto.ChatInboundMessage
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.CloseReason.Codes
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

class ChatRoomManager(
    private val chatService: ChatService,
    private val userService: UserService,
    private val subscriptionScope: CoroutineScope,
    private val broadcasterScope: CoroutineScope
) {
    private val objectMapper = jacksonObjectMapper()
    private val sessions: MutableSet<DefaultWebSocketServerSession> =
        Collections.newSetFromMap(ConcurrentHashMap<DefaultWebSocketServerSession, Boolean>())

    init {
        chatService.subscribe(subscriptionScope) { payload ->
            broadcast(payload)
        }
    }

    suspend fun handleSession(session: DefaultWebSocketServerSession, user: User) {
        sessions += session
        runCatching { sendHistory(session) }
        try {
            for (frame in session.incoming) {
                val text = (frame as? Frame.Text)?.readText() ?: continue
                val inbound = runCatching { objectMapper.readValue<ChatInboundMessage>(text) }.getOrNull() ?: continue
                val normalized = inbound.content.trim()
                if (normalized.isBlank()) continue
                val bounded = normalized.take(2000)
                val saved = chatService.appendMessage(user, bounded)
                // 활동 점수 +1 (채팅 기여)
                runCatching { userService.adjustScore(user.id, 1) }
                chatService.publish(saved)
            }
        } catch (_: Exception) {
            // Ignore errors per session
        } finally {
            sessions -= session
            runCatching { session.close(CloseReason(Codes.NORMAL, "Session closed")) }
        }
    }

    private suspend fun sendHistory(session: DefaultWebSocketServerSession) {
        val history = chatService.recentMessages()
        history.forEach { message ->
            val payload = objectMapper.writeValueAsString(message.copy(type = "history"))
            session.send(Frame.Text(payload))
        }
    }

    private fun broadcast(message: ChatBroadcastMessage) {
        val payload = objectMapper.writeValueAsString(message)
        sessions.forEach { session ->
            broadcasterScope.launch {
                runCatching {
                    session.send(Frame.Text(payload))
                }
            }
        }
    }

    fun shutdown() {
        sessions.forEach { session ->
            broadcasterScope.launch {
                runCatching { session.close(CloseReason(Codes.GOING_AWAY, "Server shutting down")) }
            }
        }
        sessions.clear()
        subscriptionScope.cancel()
        broadcasterScope.cancel()
    }
}
