package com.psmo.service

import com.psmo.database.RedisConfig
import com.psmo.model.User
import io.ktor.server.config.ApplicationConfig
import java.util.UUID

class RefreshTokenService(
    private val config: ApplicationConfig,
    private val userService: UserService
) {
    private val jedisPool by lazy { RedisConfig.createJedisPool(config) }

    fun createRefreshToken(user: User, expirationSeconds: Long): String {
        return jedisPool.resource.use { jedis ->
            val tokenString = UUID.randomUUID().toString()
            val redisKey = "refresh_token:$tokenString"
            
            // Redis SETEX: Key, Seconds, Value (UserId)
            // 자동으로 시간이 지나면 삭제됨 (Auto Expiration)
            jedis.setex(redisKey, expirationSeconds, user.id.toString())
            
            tokenString
        }
    }

    fun validateRefreshToken(tokenString: String): User? {
        return jedisPool.resource.use { jedis ->
            val redisKey = "refresh_token:$tokenString"
            val userIdString = jedis.get(redisKey) ?: return@use null
            
            val userId = userIdString.toLongOrNull() ?: return@use null
            userService.getUserById(userId)
        }
    }

    fun rotateRefreshToken(oldTokenString: String, expirationSeconds: Long): Pair<String, User>? {
        return jedisPool.resource.use { jedis ->
            val oldRedisKey = "refresh_token:$oldTokenString"
            val userIdString = jedis.get(oldRedisKey) ?: return@use null
            
            // Rotation: 기존 토큰 삭제 (RTR)
            jedis.del(oldRedisKey)
            
            val userId = userIdString.toLongOrNull() ?: return@use null
            val user = userService.getUserById(userId) ?: return@use null

            // 새 토큰 발급
            val newTokenString = UUID.randomUUID().toString()
            val newRedisKey = "refresh_token:$newTokenString"
            
            jedis.setex(newRedisKey, expirationSeconds, user.id.toString())
            
            Pair(newTokenString, user)
        }
    }

    fun revokeRefreshToken(tokenString: String) {
        jedisPool.resource.use { jedis ->
            val redisKey = "refresh_token:$tokenString"
            jedis.del(redisKey)
        }
    }
    
    // 단순 UUID 방식에서는 "특정 사용자의 모든 토큰"을 찾기가 어렵습니다 (SCAN 필요).
    // 필요하다면 userId -> Set<Token> 역인덱싱을 관리해야 합니다.
    // 현재 요구사항에서는 필수 아님.
    fun revokeAllUserTokens(userId: Long) {
        // Not implemented for simple UUID pattern without secondary index
    }
}
