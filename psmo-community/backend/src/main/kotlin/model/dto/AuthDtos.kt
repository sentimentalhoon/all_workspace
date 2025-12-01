package com.psmo.model.dto

import com.psmo.model.User

data class TokenResponse(
    val accessToken: String,
    val expiresAt: Long
)

data class UserResponse(
    val id: Long,
    val telegramId: Long,
    val displayName: String?,
    val username: String?,
    val photoUrl: String?
)

data class ProfileResponse(
    val status: String = "success",
    val user: UserResponse
)

fun User.toResponse(): UserResponse = UserResponse(
    id = id,
    telegramId = telegramId,
    displayName = displayName,
    username = username,
    photoUrl = photoUrl
)
