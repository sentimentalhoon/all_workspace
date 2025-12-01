package com.psmo.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Users : LongIdTable("users") {
    val name = varchar("name", 100)
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val role = varchar("role", 20).default("USER")
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }
}

object RefreshTokens : LongIdTable("refresh_tokens") {
    val token = varchar("token", 500).uniqueIndex()
    val userId = reference("user_id", Users)
    val expiryDate = datetime("expiry_date")
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
}

@Serializable
data class User(
    val id: Long? = null,
    val name: String,
    val email: String,
    val password: String? = null,
    val role: String = "USER"
)

@Serializable
data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    val createdAt: String
)
