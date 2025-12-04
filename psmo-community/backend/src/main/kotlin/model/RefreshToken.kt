package com.psmo.model

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object RefreshTokens : LongIdTable("refresh_tokens") {
    val userId = reference("user_id", Users)
    val token = varchar("token", 255).uniqueIndex()
    val expiresAt = timestamp("expires_at")
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentTimestamp())
}

class RefreshToken(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<RefreshToken>(RefreshTokens)

    var user by User referencedOn RefreshTokens.userId
    var token by RefreshTokens.token
    var expiresAt by RefreshTokens.expiresAt
    var createdAt by RefreshTokens.createdAt
}
