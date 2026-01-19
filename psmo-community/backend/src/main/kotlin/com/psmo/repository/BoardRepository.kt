package com.psmo.repository
 
import com.psmo.database.DatabaseConfig
import com.psmo.model.*
import com.psmo.model.dto.BoardCategory
import com.psmo.model.dto.PostResponse
import com.psmo.model.dto.CommentResponse
import com.psmo.model.dto.toResponse
import com.psmo.model.dto.PostCreateRequest
import io.ktor.server.config.ApplicationConfig
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class BoardRepository(private val config: ApplicationConfig) {
    private val database by lazy { DatabaseConfig.connectToDatabase(config) }

    suspend fun createPost(userId: Long, request: PostCreateRequest): PostResponse = newSuspendedTransaction(Dispatchers.IO) {
        val user = UserEntity.findById(userId) ?: throw IllegalArgumentException("User not found")
        val post = Post.new {
            this.title = request.title
            this.content = request.content
            this.category = request.category
            this.author = user
        }
        
        request.imageUrls.forEachIndexed { index, url ->
            PostImage.new {
                this.post = post
                this.url = url
                this.orderIndex = index
            }
        }
        
        // Eager load and map to DTO inside transaction
        PostResponse(
            post.id.value, post.title, post.content, post.category, post.author.toDomain().toResponse(),
            post.viewCount, post.likeCount, post.comments.count(), post.createdAt.toString(),
            post.images.sortedBy { it.orderIndex }.map { it.url },
            false
        )
    }

    suspend fun findPosts(page: Int, size: Int, category: BoardCategory?): List<PostResponse> = newSuspendedTransaction(Dispatchers.IO) {
        val query = Posts.selectAll()
        category?.let {
            query.andWhere { Posts.category eq it }
        }
        
        val sortedQuery = query.orderBy(Posts.createdAt to SortOrder.DESC)
            .limit(size)
            .offset(((page - 1) * size).toLong())
        
        Post.wrapRows(sortedQuery).map { post ->
             PostResponse(
                post.id.value, post.title, post.content, post.category, post.author.toDomain().toResponse(),
                post.viewCount, post.likeCount, post.comments.count(), post.createdAt.toString(),
                post.images.sortedBy { it.orderIndex }.map { it.url },
                false
            )
        }.toList()
    }

    suspend fun findPostById(id: Long): PostResponse? = newSuspendedTransaction(Dispatchers.IO) {
        val post = Post.findById(id) ?: return@newSuspendedTransaction null
        PostResponse(
            post.id.value, post.title, post.content, post.category, post.author.toDomain().toResponse(),
            post.viewCount, post.likeCount, post.comments.count(), post.createdAt.toString(),
            post.images.sortedBy { it.orderIndex }.map { it.url },
            false
        )
    }

    suspend fun createComment(postId: Long, userId: Long, content: String): CommentResponse = newSuspendedTransaction(Dispatchers.IO) {
        val post = Post.findById(postId) ?: throw IllegalArgumentException("Post not found")
        val user = UserEntity.findById(userId) ?: throw IllegalArgumentException("User not found")
        
        val comment = Comment.new {
            this.post = post
            this.author = user
            this.content = content
        }
        
        CommentResponse(
            comment.id.value, comment.content, comment.author.toDomain().toResponse(), comment.createdAt.toString()
        )
    }
    
    suspend fun findComments(postId: Long): List<CommentResponse> = newSuspendedTransaction(Dispatchers.IO) {
        Comment.find { Comments.postId eq postId }
            .orderBy(Comments.createdAt to SortOrder.ASC)
            .map {
                CommentResponse(
                    it.id.value, it.content, it.author.toDomain().toResponse(), it.createdAt.toString()
                )
            }.toList()
    }

    suspend fun toggleLike(postId: Long, userId: Long): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        // Returns true if liked, false if unliked
        val existing = PostLikes.selectAll().where { (PostLikes.postId eq postId) and (PostLikes.userId eq userId) }.count() > 0
        if (existing) {
            PostLikes.deleteWhere { (PostLikes.postId eq postId) and (PostLikes.userId eq userId) }
            Posts.update({ Posts.id eq postId }) {
                with(SqlExpressionBuilder) {
                    it.update(likeCount, likeCount - 1)
                }
            }
            false
        } else {
            PostLikes.insert {
                it[PostLikes.postId] = postId
                it[PostLikes.userId] = userId
            }
            Posts.update({ Posts.id eq postId }) {
                with(SqlExpressionBuilder) {
                    it.update(likeCount, likeCount + 1)
                }
            }
            true
        }
    }
    
    suspend fun isLiked(postId: Long, userId: Long?): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        if (userId == null) return@newSuspendedTransaction false
        PostLikes.selectAll().where { (PostLikes.postId eq postId) and (PostLikes.userId eq userId) }.count() > 0
    }

    suspend fun incrementViewCount(postId: Long) = newSuspendedTransaction(Dispatchers.IO) {
        Posts.update({ Posts.id eq postId }) {
            with(SqlExpressionBuilder) {
                it.update(viewCount, viewCount + 1)
            }
        }
    }
}
