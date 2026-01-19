package com.psmo.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class BadUserCreateRequest(
    val region: String,
    val reason: String,
    val physicalDescription: String? = null,
    val incidentDate: String? = null // ISO 8601 format: "2026-01-19"
)

@Serializable
data class BadUserResponse(
    val id: Long,
    val region: String,
    val reason: String,
    val physicalDescription: String?,
    val incidentDate: String?,
    val images: List<BadUserImageResponse>,
    val reporterName: String,
    val reporterId: Long,
    val createdAt: String
)

@Serializable
data class BadUserImageResponse(
    val id: Long,
    val imageUrl: String,
    val thumbnailUrl: String
)

@Serializable
data class BadUserImageDto(
    val id: Long,
    val imageUrl: String
)

@Serializable
data class BadUserUpdateRequest(
    val region: String? = null,
    val reason: String? = null,
    val physicalDescription: String? = null,
    val incidentDate: String? = null
)
