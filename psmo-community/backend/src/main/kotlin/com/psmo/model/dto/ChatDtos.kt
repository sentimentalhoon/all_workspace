package com.psmo.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.psmo.model.ChatMessage
import java.time.ZoneOffset

/**
 * WebSocket 클라이언트가 전송하는 메시지 포맷.
 */
data class ChatInboundMessage(
    val type: String = "chat_message",
    val content: String
)

/**
 * 모든 구독자에게 방송되는 메시지 포맷.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ChatBroadcastMessage(
    val type: String = "chat_message",
    val messageId: Long,
    val userId: Long,
    val displayName: String?,
    val username: String?,
    val content: String,
    val createdAtEpochMillis: Long
)

fun ChatMessage.toBroadcast(): ChatBroadcastMessage = ChatBroadcastMessage(
    messageId = id,
    userId = userId,
    displayName = displayName,
    username = username,
    content = content,
    createdAtEpochMillis = createdAt.toInstant(ZoneOffset.UTC).toEpochMilli()
)
