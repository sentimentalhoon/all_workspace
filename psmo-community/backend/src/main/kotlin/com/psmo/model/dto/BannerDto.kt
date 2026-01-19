package com.psmo.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class BannerResponse(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val linkUrl: String?,
    val isVisible: Boolean,
    val orderIndex: Int,
    val createdAt: String
)

@Serializable
data class BannerCreateRequest(
    val title: String,
    val linkUrl: String?,
    val isVisible: Boolean,
    val orderIndex: Int
)

@Serializable
data class BannerUpdateRequest(
    val title: String?,
    val linkUrl: String?,
    val isVisible: Boolean?,
    val orderIndex: Int?
)

@Serializable
data class BannerOrderRequest(
    val id: Long,
    val newOrder: Int
)
