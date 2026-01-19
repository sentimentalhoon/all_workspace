package com.psmo.model.dto

import kotlinx.serialization.Serializable

@Serializable
@Serializable
enum class BoardCategory {
    NOTICE, FREE, QA
}

@Serializable
enum class BoardSubCategory {
    // Notice
    MUST_READ, UPDATE, EVENT,
    // Free
    CHAT, HUMOR, INFO,
    // QA
    HARDWARE, SOFTWARE, OPERATION, ETC
}

@Serializable
data class PostCreateRequest(
    val title: String,
    val content: String,
    val category: BoardCategory,
    val subCategory: BoardSubCategory? = null,
    val imageUrls: List<String> = emptyList() // Optional images in post
)

@Serializable
data class PostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val category: BoardCategory,
    val subCategory: BoardSubCategory? = null,
    val author: UserResponse,
    val viewCount: Int,
    val likeCount: Int,
    val commentCount: Long,
    val createdAt: String,
    val imageUrls: List<String>,
    val isLiked: Boolean = false // Current user liked?
)

@Serializable
data class CommentCreateRequest(
    val content: String
)

@Serializable
data class CommentResponse(
    val id: Long,
    val content: String,
    val author: UserResponse,
    val createdAt: String
)
