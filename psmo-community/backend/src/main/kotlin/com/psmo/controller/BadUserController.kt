package com.psmo.controller

import com.psmo.model.User
import com.psmo.model.dto.BadUserCreateRequest
import com.psmo.resources.BadUserCreateResource
import com.psmo.resources.BadUserListResource
import com.psmo.service.BadUserService
import com.psmo.service.ImageService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.resources.delete
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import io.ktor.utils.io.jvm.javaio.toInputStream

fun Route.badUserRoutes(badUserService: BadUserService, imageService: ImageService) {
    // 조회 (공개 - 로그인 불필요)
    get<BadUserListResource> { resource ->
        val result = badUserService.searchBadUsers(resource.keyword)
        call.respond(result)
    }

    // 등록 (로그인 필요)


    // 상세 조회 (공개)
    get<BadUserCreateResource.Id> { resource ->
        try {
            val result = badUserService.getBadUserById(resource.id)
            call.respond(result)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.NotFound, mapOf("error" to "존재하지 않는 게시글입니다."))
        }
    }

    authenticate("auth-jwt") {
        // ... POST logic (existing) ...
        post<BadUserCreateResource> {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.subject?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "인증 필요"))

            // Multipart Request 처리
            val multipart = call.receiveMultipart()
            var createRequest: BadUserCreateRequest? = null
            val uploadedImages = mutableListOf<ImageService.ImageUploadResult>()

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "data") {
                            createRequest = Json.decodeFromString<BadUserCreateRequest>(part.value)
                        }
                    }
                    is PartData.FileItem -> {
                        val fileName = part.originalFileName ?: "image.jpg"
                        val contentType = part.contentType?.toString() ?: "image/jpeg"
                        val bytes = part.provider().toInputStream().use { it.readBytes() }
                        val result = imageService.uploadImageWithThumbnail(bytes, fileName, contentType)
                        uploadedImages.add(result)
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (createRequest == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "데이터가 누락되었습니다."))
                return@post
            }

            if (uploadedImages.size > 20) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "사진은 최대 20장까지만 첨부할 수 있습니다."))
                return@post
            }

            try {
                // User object for service (requires limited fields mostly id)
                val user = User(id = userId, telegramId = 0, displayName = null, username = null, photoUrl = null, role = com.psmo.model.UserRole.MEMBER, score = 0, activityLevel = 0, createdAt = java.time.LocalDateTime.now(), updatedAt = java.time.LocalDateTime.now())
                val response = badUserService.reportBadUser(user, createRequest!!, uploadedImages)
                call.respond(HttpStatusCode.Created, response)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "요청 처리 실패")))
            }
        }

        // 수정
        put<BadUserCreateResource.Id> { resource ->
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.subject?.toLongOrNull()
                ?: return@put call.respond(HttpStatusCode.Unauthorized)
            
            val multipart = call.receiveMultipart()
            var updateRequest: com.psmo.model.dto.BadUserUpdateRequest? = null
            val newImages = mutableListOf<ImageService.ImageUploadResult>()
            var deleteImageIds: List<Long> = emptyList()

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "data") {
                            updateRequest = Json.decodeFromString<com.psmo.model.dto.BadUserUpdateRequest>(part.value)
                        } else if (part.name == "deleteImageIds") {
                             deleteImageIds = Json.decodeFromString<List<Long>>(part.value)
                        }
                    }
                    is PartData.FileItem -> {
                        val fileName = part.originalFileName ?: "image.jpg"
                        val contentType = part.contentType?.toString() ?: "image/jpeg"
                        val bytes = part.provider().toInputStream().use { it.readBytes() }
                        val result = imageService.uploadImageWithThumbnail(bytes, fileName, contentType)
                        newImages.add(result)
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (updateRequest == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "데이터가 누락되었습니다."))
                return@put
            }

            try {
                // Fetch User role for admin check if needed, but here simple user object is passed
                // Ideally should fetch full user from DB to check role, but Service can fetch user or we pass basic user.
                // Service expects User object. For role check, we need role from JWT or DB.
                // JWT claims usually have role.
                val roleStr = principal.payload.getClaim("role").asString() ?: "MEMBER"
                val role = try { com.psmo.model.UserRole.valueOf(roleStr) } catch(e:Exception) { com.psmo.model.UserRole.MEMBER }
                
                val user = User(id = userId, telegramId = 0, displayName = null, username = null, photoUrl = null, role = role, score = 0, activityLevel = 0, createdAt = java.time.LocalDateTime.now(), updatedAt = java.time.LocalDateTime.now())
                
                val response = badUserService.updateBadUser(resource.id, user, updateRequest!!, newImages, deleteImageIds)
                call.respond(response)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "수정 실패")))
            }
        }

        // 삭제
        delete<BadUserCreateResource.Id> { resource ->
             val principal = call.principal<JWTPrincipal>()
             val userId = principal?.subject?.toLongOrNull()
                ?: return@delete call.respond(HttpStatusCode.Unauthorized)

             try {
                val roleStr = principal.payload.getClaim("role").asString() ?: "MEMBER"
                val role = try { com.psmo.model.UserRole.valueOf(roleStr) } catch(e:Exception) { com.psmo.model.UserRole.MEMBER }
                val user = User(id = userId, telegramId = 0, displayName = null, username = null, photoUrl = null, role = role, score = 0, activityLevel = 0, createdAt = java.time.LocalDateTime.now(), updatedAt = java.time.LocalDateTime.now())

                badUserService.deleteBadUser(resource.id, user)
                call.respond(HttpStatusCode.OK, mapOf("status" to "deleted"))
             } catch (e: Exception) {
                 call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "삭제 실패")))
             }
        }
    }
}
