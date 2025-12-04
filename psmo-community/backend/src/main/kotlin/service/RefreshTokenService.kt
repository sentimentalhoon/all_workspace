package com.psmo.service

import com.psmo.database.DatabaseConfig
import com.psmo.model.RefreshToken
import com.psmo.model.RefreshTokens
import com.psmo.model.User
import com.psmo.model.Users
import com.psmo.model.toUser
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.UUID

class RefreshTokenService {

    fun createRefreshToken(user: User, expirationSeconds: Long): String {
        return transaction {
            val tokenString = UUID.randomUUID().toString()
            RefreshToken.new {
                this.userId = org.jetbrains.exposed.dao.id.EntityID(user.id, Users)
                this.token = tokenString
                this.expiresAt = LocalDateTime.now().plusSeconds(expirationSeconds)
            }
            tokenString
        }
    }

    fun validateRefreshToken(tokenString: String): User? {
        return transaction {
            val refreshToken = RefreshToken.find { RefreshTokens.token eq tokenString }.singleOrNull()
            
            if (refreshToken == null) return@transaction null
            
            if (refreshToken.expiresAt.isBefore(LocalDateTime.now())) {
                refreshToken.delete() // 만료된 토큰 삭제
                return@transaction null
            }

            Users.select { Users.id eq refreshToken.userId }
                .singleOrNull()
                ?.toUser()
        }
    }

    fun rotateRefreshToken(oldTokenString: String, expirationSeconds: Long): Pair<String, User>? {
        return transaction {
            val refreshToken = RefreshToken.find { RefreshTokens.token eq oldTokenString }.singleOrNull()
            
            if (refreshToken == null) return@transaction null
            
            val user = Users.select { Users.id eq refreshToken.userId }
                .singleOrNull()
                ?.toUser()
            
            if (user == null) {
                refreshToken.delete()
                return@transaction null
            }

            // 기존 토큰 삭제 (Rotation)
            refreshToken.delete()

            // 새 토큰 발급
            val newTokenString = UUID.randomUUID().toString()
            RefreshToken.new {
                this.userId = org.jetbrains.exposed.dao.id.EntityID(user.id, Users)
                this.token = newTokenString
                this.expiresAt = LocalDateTime.now().plusSeconds(expirationSeconds)
            }
            Pair(newTokenString, user)
        }
    }

    fun revokeRefreshToken(tokenString: String) {
        transaction {
            RefreshToken.find { RefreshTokens.token eq tokenString }.singleOrNull()?.delete()
        }
    }
    
    fun revokeAllUserTokens(userId: Long) {
        transaction {
             RefreshToken.find { RefreshTokens.userId eq userId }.forEach { it.delete() }
        }
    }
}
