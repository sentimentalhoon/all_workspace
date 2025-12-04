package com.psmo.service

import com.psmo.database.DatabaseConfig
import com.psmo.model.RefreshToken
import com.psmo.model.RefreshTokens
import com.psmo.model.User
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

class RefreshTokenService {

    fun createRefreshToken(user: User, expirationSeconds: Long): String {
        return transaction {
            val tokenString = UUID.randomUUID().toString()
            RefreshToken.new {
                this.user = user
                this.token = tokenString
                this.expiresAt = Instant.now().plusSeconds(expirationSeconds)
            }
            tokenString
        }
    }

    fun validateRefreshToken(tokenString: String): User? {
        return transaction {
            val refreshToken = RefreshToken.find { RefreshTokens.token eq tokenString }.singleOrNull()
            
            if (refreshToken == null) return@transaction null
            
            if (refreshToken.expiresAt.isBefore(Instant.now())) {
                refreshToken.delete() // 만료된 토큰 삭제
                return@transaction null
            }

            refreshToken.user
        }
    }

    fun rotateRefreshToken(oldTokenString: String, expirationSeconds: Long): Pair<String, User>? {
        return transaction {
            val refreshToken = RefreshToken.find { RefreshTokens.token eq oldTokenString }.singleOrNull()
            
            if (refreshToken == null) return@transaction null
            
            // 기존 토큰 삭제 (Rotation)
            val user = refreshToken.user
            refreshToken.delete()

            // 새 토큰 발급
            val newTokenString = UUID.randomUUID().toString()
            RefreshToken.new {
                this.user = user
                this.token = newTokenString
                this.expiresAt = Instant.now().plusSeconds(expirationSeconds)
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
