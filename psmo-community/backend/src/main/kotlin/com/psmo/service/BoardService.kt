package com.psmo.service

import com.psmo.model.dto.*
// import com.psmo.model.toResponse -- Removed, explicitly allow usage from dto package
import com.psmo.repository.BoardRepository

import com.psmo.model.dto.BoardSubCategory

class BoardService(private val repository: BoardRepository) {

    suspend fun createPost(userId: Long, request: PostCreateRequest): PostResponse {
        return repository.createPost(userId, request)
    }

    suspend fun getPosts(page: Int, size: Int, category: BoardCategory?, subCategory: BoardSubCategory?): List<PostResponse> {
        return repository.findPosts(page, size, category, subCategory)
    }

    suspend fun getPostDetail(id: Long, userId: Long?): PostResponse? {
        val post = repository.findPostById(id) ?: return null
        repository.incrementViewCount(id)
        val isLiked = repository.isLiked(id, userId)
        
        return post.copy(isLiked = isLiked)
    }

    suspend fun addComment(postId: Long, userId: Long, content: String): CommentResponse {
        return repository.createComment(postId, userId, content)
    }
    
    suspend fun getComments(postId: Long): List<CommentResponse> {
        return repository.findComments(postId)
    }

    suspend fun toggleLike(postId: Long, userId: Long): Boolean {
        return repository.toggleLike(postId, userId)
    }

    suspend fun updatePost(postId: Long, userId: Long, role: String, request: PostCreateRequest): PostResponse {
        val post = repository.findPostById(postId) ?: throw IllegalArgumentException("Post not found")
        
        // Permission Check
        if (post.author.id != userId && role != "ADMIN") {
             throw IllegalStateException("Forbidden")
        }
        
        return repository.updatePost(postId, request) ?: throw IllegalArgumentException("Post not found")
    }

    suspend fun deletePost(postId: Long, userId: Long, role: String) {
        val post = repository.findPostById(postId) ?: throw IllegalArgumentException("Post not found")
        
        if (post.author.id != userId && role != "ADMIN") {
             throw IllegalStateException("Forbidden")
        }
        
        repository.deletePost(postId)
    }
}
