package com.psmo.model.dto

import kotlinx.serialization.Serializable
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
/**
 * 화면(프론트엔드)에 보여주기 위한 사용자 정보입니다.
 * 비밀번호 같은 민감한 정보는 빼고, 이름이나 사진 주소 같은 것만 담습니다.
 */
@Serializable
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
