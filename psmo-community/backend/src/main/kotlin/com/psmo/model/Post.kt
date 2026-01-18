package com.psmo.model

import com.psmo.model.dto.BoardCategory
import com.psmo.model.dto.PostResponse
import com.psmo.model.dto.UserResponse
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object Posts : LongIdTable("posts") {
    val title = varchar("title", 255)
    val content = text("content")
    val authorId = reference("author_id", Users)
    val category = enumerationByName("category", 20, BoardCategory::class)
    val viewCount = integer("view_count").default(0)
    val likeCount = integer("like_count").default(0) // Denormalized for pref
    val createdAt = datetime("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentDateTime)
}

class Post(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Post>(Posts)

    var title by Posts.title
    var content by Posts.content
    var author by User referencedOn Posts.authorId
    var category by Posts.category
    var viewCount by Posts.viewCount
    var likeCount by Posts.likeCount
    var createdAt by Posts.createdAt
    val comments by Comment referrersOn Comments.postId
    val images by PostImage referrersOn PostImages.postId
}

object PostImages : LongIdTable("post_images") {
    val postId = reference("post_id", Posts)
    val url = varchar("url", 255)
    val orderIndex = integer("order_index").default(0)
}

class PostImage(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PostImage>(PostImages)
    var post by Post referencedOn PostImages.postId
    var url by PostImages.url
    var orderIndex by PostImages.orderIndex
}

object Comments : LongIdTable("comments") {
    val postId = reference("post_id", Posts)
    val authorId = reference("author_id", Users)
    val content = text("content")
    val createdAt = datetime("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentDateTime)
}

class Comment(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Comment>(Comments)

    var post by Post referencedOn Comments.postId
    var author by User referencedOn Comments.authorId
    var content by Comments.content
    var createdAt by Comments.createdAt
}

object PostLikes : LongIdTable("post_likes") {
    val postId = reference("post_id", Posts)
    val userId = reference("user_id", Users)
    
    init {
        uniqueIndex(postId, userId)
    }
}
