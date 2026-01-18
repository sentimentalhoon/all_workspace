package com.psmo.service

import com.psmo.model.dto.*
// import com.psmo.model.toResponse -- Removed, explicitly allow usage from dto package
import com.psmo.repository.BoardRepository

class BoardService(private val repository: BoardRepository) {

    suspend fun createPost(userId: Long, request: PostCreateRequest): PostResponse {
        val post = repository.createPost(userId, request)
        return PostResponse(
            post.id.value, post.title, post.content, post.category, post.author.toDomain().toResponse(),
            post.viewCount, post.likeCount, post.comments.count(), post.createdAt.toString(),
            post.images.sortedBy { it.orderIndex }.map { it.url },
            false
        )
    }

    suspend fun getPosts(page: Int, size: Int, category: BoardCategory?): List<PostResponse> {
        return repository.findPosts(page, size, category).map { post ->
            PostResponse(
                post.id.value, post.title, post.content, post.category, post.author.toDomain().toResponse(),
                post.viewCount, post.likeCount, post.comments.count(), post.createdAt.toString(),
                post.images.sortedBy { it.orderIndex }.map { it.url },
                false // List view doesn't usually check "isLiked" for performance, or check separately if needed
            )
        }
    }

    suspend fun getPostDetail(id: Long, userId: Long?): PostResponse? {
        val post = repository.findPostById(id) ?: return null
        repository.incrementViewCount(id)
        val isLiked = repository.isLiked(id, userId)
        
        return PostResponse(
            post.id.value, post.title, post.content, post.category, post.author.toDomain().toResponse(),
            post.viewCount, post.likeCount, post.comments.count(), post.createdAt.toString(),
            post.images.sortedBy { it.orderIndex }.map { it.url },
            isLiked
        )
    }

    suspend fun addComment(postId: Long, userId: Long, content: String): CommentResponse {
        val comment = repository.createComment(postId, userId, content)
        return CommentResponse(
            comment.id.value, comment.content, comment.author.toDomain().toResponse(), comment.createdAt.toString()
        )
    }
    
    suspend fun getComments(postId: Long): List<CommentResponse> {
        return repository.findComments(postId).map {
             CommentResponse(
                it.id.value, it.content, it.author.toDomain().toResponse(), it.createdAt.toString()
            )
        }
    }

    suspend fun toggleLike(postId: Long, userId: Long): Boolean {
        return repository.toggleLike(postId, userId)
    }
}
