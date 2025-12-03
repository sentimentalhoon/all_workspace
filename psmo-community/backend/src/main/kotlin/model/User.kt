package com.psmo.model

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * Telegram 사용자 테이블 정의.
 */
object Users : LongIdTable("users") {
    val telegramId = long("telegram_id").uniqueIndex()
    val displayName = varchar("display_name", 150).nullable()
    val username = varchar("username", 150).nullable()
    val photoUrl = varchar("photo_url", 512).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}

/**
 * 사용자 도메인 모델.
 * TODO: 역할/권한 필드(role 등)를 추가하여 RBAC 구현
 */
data class User(
    val id: Long,
    val telegramId: Long,
    val displayName: String?,
    val username: String?,
    val photoUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

/**
 * Exposed ResultRow -> User 변환 헬퍼.
 */
fun ResultRow.toUser(): User = User(
    id = this[Users.id].value,
    telegramId = this[Users.telegramId],
    displayName = this[Users.displayName],
    username = this[Users.username],
    photoUrl = this[Users.photoUrl],
    createdAt = this[Users.createdAt],
    updatedAt = this[Users.updatedAt]
)
