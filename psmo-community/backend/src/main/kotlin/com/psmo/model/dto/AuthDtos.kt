package com.psmo.model.dto

import com.psmo.model.User

/**
 * JWT 발급 결과.
 */
data class TokenResponse(
    val accessToken: String,
    val expiresAt: Long
)

/**
 * 클라이언트에 노출되는 사용자 정보.
 */
data class UserResponse(
    val id: Long,
    val telegramId: Long,
    val displayName: String?,
    val username: String?,
    val photoUrl: String?,
    val role: String,
    val score: Int,
    val activityLevel: Int
)

/**
 * `/api/me` 응답 DTO.
 */
data class ProfileResponse(
    val status: String = "success",
    val user: UserResponse
)

/**
 * User -> API 응답 변환 헬퍼.
 */
fun User.toResponse(): UserResponse = UserResponse(
    id = id,
    telegramId = telegramId,
    displayName = displayName,
    username = username,
    photoUrl = photoUrl,
    role = role.name.lowercase(),
    score = score,
    activityLevel = activityLevel
)
