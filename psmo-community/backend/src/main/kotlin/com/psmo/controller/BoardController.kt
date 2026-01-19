package com.psmo.controller

import com.psmo.model.dto.CommentCreateRequest
import com.psmo.model.dto.PostCreateRequest
import com.psmo.resources.BoardResources
import com.psmo.service.BoardService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.resources.delete
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.boardRoutes(service: BoardService) {

    get<BoardResources.Posts> { params ->
        val categoryIdx = params.category?.let { 
             try { com.psmo.model.dto.BoardCategory.valueOf(it) } catch(e:Exception) { null } 
        }
        val subCategoryIdx = params.subCategory?.let {
             try { com.psmo.model.dto.BoardSubCategory.valueOf(it) } catch(e:Exception) { null }
        }
        val posts = service.getPosts(params.page, params.size, categoryIdx, subCategoryIdx)
        call.respond(mapOf("status" to "success", "data" to posts))
    }

    get<BoardResources.Posts.Id> { params ->
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.subject?.toLongOrNull()
        
        val post = service.getPostDetail(params.id, userId)
        if (post != null) {
            call.respond(mapOf("status" to "success", "data" to post))
        } else {
            call.respond(HttpStatusCode.NotFound, mapOf("status" to "error", "message" to "Post not found"))
        }
    }
    
    get<BoardResources.Posts.Id.Comments> { params ->
        val comments = service.getComments(params.parent.id)
        call.respond(mapOf("status" to "success", "data" to comments))
    }

    authenticate("auth-jwt") {
        post<BoardResources.Posts> {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.subject?.toLongOrNull()
            val role = principal?.payload?.getClaim("role")?.asString() ?: "MEMBER"

            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "error", "message" to "Invalid User Token"))
                return@post
            }

            val request = call.receive<PostCreateRequest>()

            if (request.category == com.psmo.model.dto.BoardCategory.NOTICE && role != com.psmo.model.UserRole.ADMIN.name) {
                call.respond(HttpStatusCode.Forbidden, mapOf("status" to "error", "message" to "Admin only for NOTICE"))
                return@post
            }
            
            val created = service.createPost(userId, request)
            call.respond(HttpStatusCode.Created, mapOf("status" to "success", "data" to created))
        }

        post<BoardResources.Posts.Id.Comments> { params ->
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.subject?.toLongOrNull()
            
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "error", "message" to "Invalid Token"))
                return@post
            }

            val request = call.receive<CommentCreateRequest>()
            
            val comment = service.addComment(params.parent.id, userId, request.content)
            call.respond(HttpStatusCode.Created, mapOf("status" to "success", "data" to comment))
        }

        post<BoardResources.Posts.Id.Like> { params ->
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.subject?.toLongOrNull()

            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "error", "message" to "Invalid Token"))
                return@post
            }
            
            val isLiked = service.toggleLike(params.parent.id, userId)
            call.respond(mapOf("status" to "success", "isLiked" to isLiked))
        }

        put<BoardResources.Posts.Id> { params ->
             val principal = call.principal<JWTPrincipal>()
             val userId = principal?.subject?.toLongOrNull()
             val role = principal?.payload?.getClaim("role")?.asString() ?: "MEMBER"

             if (userId == null) {
                 call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "error", "message" to "Invalid Token"))
                 return@put
             }

             val request = call.receive<PostCreateRequest>()
             
             try {
                 val updated = service.updatePost(params.id, userId, role, request)
                 call.respond(mapOf("status" to "success", "data" to updated))
             } catch (e: IllegalStateException) {
                 call.respond(HttpStatusCode.Forbidden, mapOf("status" to "error", "message" to e.message))
             } catch (e: IllegalArgumentException) {
                 call.respond(HttpStatusCode.NotFound, mapOf("status" to "error", "message" to e.message))
             }
        }

        delete<BoardResources.Posts.Id> { params ->
             val principal = call.principal<JWTPrincipal>()
             val userId = principal?.subject?.toLongOrNull()
             val role = principal?.payload?.getClaim("role")?.asString() ?: "MEMBER"

             if (userId == null) {
                 call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "error", "message" to "Invalid Token"))
                 return@delete
             }
             
             try {
                 service.deletePost(params.id, userId, role)
                 call.respond(mapOf("status" to "success", "message" to "Deleted successfully"))
             } catch (e: IllegalStateException) {
                 call.respond(HttpStatusCode.Forbidden, mapOf("status" to "error", "message" to e.message))
             } catch (e: IllegalArgumentException) {
                 call.respond(HttpStatusCode.NotFound, mapOf("status" to "error", "message" to e.message))
             }
        }
    }
}
