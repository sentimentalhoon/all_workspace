package com.psmo.model

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * 실시간 채팅 메시지 테이블 정의.
 */
object ChatMessages : LongIdTable("chat_messages") {
    val userId = reference("user_id", Users)
    val displayName = varchar("display_name", 150).nullable()
    val username = varchar("username", 150).nullable()
    val content = text("content")
    val createdAt = datetime("created_at")
}

/**
 * 채팅 메시지 도메인 모델.
 */
data class ChatMessage(
    val id: Long,
    val userId: Long,
    val displayName: String?,
    val username: String?,
    val content: String,
    val createdAt: LocalDateTime
)

/**
 * ResultRow -> ChatMessage 변환 헬퍼.
 */
fun ResultRow.toChatMessage(): ChatMessage = ChatMessage(
    id = this[ChatMessages.id].value,
    userId = this[ChatMessages.userId].value,
    displayName = this[ChatMessages.displayName],
    username = this[ChatMessages.username],
    content = this[ChatMessages.content],
    createdAt = this[ChatMessages.createdAt]
)
