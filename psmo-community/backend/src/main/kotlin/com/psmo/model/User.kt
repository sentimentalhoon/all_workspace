package com.psmo.model

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

enum class UserRole {
    SYSTEM,
    ADMIN,
    MANAGER,
    MODERATOR,
    MEMBER
}

object Users : LongIdTable("users") {
    val telegramId = long("telegram_id").uniqueIndex()
    val displayName = varchar("display_name", 150).nullable()
    val username = varchar("username", 150).nullable()
    val photoUrl = varchar("photo_url", 512).nullable()
    val role = enumerationByName("role", 20, UserRole::class)
        .default(UserRole.MEMBER)
    val score = integer("score").default(0)
    val activityLevel = integer("activity_level").default(1)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}

// Domain Model (Data Class)
data class User(
    val id: Long,
    val telegramId: Long,
    val displayName: String?,
    val username: String?,
    val photoUrl: String?,
    val role: UserRole,
    val score: Int,
    val activityLevel: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

// Exposed Entity (DAO) - Added for relation support
class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(Users)

    var telegramId by Users.telegramId
    var displayName by Users.displayName
    var username by Users.username
    var photoUrl by Users.photoUrl
    var role by Users.role
    var score by Users.score
    var activityLevel by Users.activityLevel
    var createdAt by Users.createdAt
    var updatedAt by Users.updatedAt
    
    fun toDomain(): User = User(
        id = this.id.value,
        telegramId = this.telegramId,
        displayName = this.displayName,
        username = this.username,
        photoUrl = this.photoUrl,
        role = this.role,
        score = this.score,
        activityLevel = this.activityLevel,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun ResultRow.toUser(): User = User(
    id = this[Users.id].value,
    telegramId = this[Users.telegramId],
    displayName = this[Users.displayName],
    username = this[Users.username],
    photoUrl = this[Users.photoUrl],
    role = this[Users.role],
    score = this[Users.score],
    activityLevel = this[Users.activityLevel],
    createdAt = this[Users.createdAt],
    updatedAt = this[Users.updatedAt]
)
