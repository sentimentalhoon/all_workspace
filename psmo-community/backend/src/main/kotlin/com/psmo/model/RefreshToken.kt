package com.psmo.model

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object RefreshTokens : LongIdTable("refresh_tokens") {
    val userId = reference("user_id", Users)
    val token = varchar("token", 255).uniqueIndex()
    val expiresAt = datetime("expires_at")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}

class RefreshToken(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<RefreshToken>(RefreshTokens)

    var userId by RefreshTokens.userId
    var token by RefreshTokens.token
    var expiresAt by RefreshTokens.expiresAt
    var createdAt by RefreshTokens.createdAt
}
