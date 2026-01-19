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
    authenticate("auth-jwt", optional = true) {
        // 조회 (공개 - 로그인 불필요, 로그인 시 원본 이미지)
        get<BadUserListResource> { resource ->
            val principal = call.principal<JWTPrincipal>()
            val viewerId = principal?.subject?.toLongOrNull()
            val result = badUserService.searchBadUsers(resource.keyword, viewerId)
            call.respond(result)
        }

        // 상세 조회 (공개 - 로그인 불필요, 로그인 시 원본 이미지)
        get<BadUserCreateResource.Id> { resource ->
            val principal = call.principal<JWTPrincipal>()
            val viewerId = principal?.subject?.toLongOrNull()
            try {
                val result = badUserService.getBadUserById(resource.id, viewerId)
                call.respond(result)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "존재하지 않는 게시글입니다."))
            }
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
            // Map to store paired images: Index -> { "original": Meta, "blur": Bytes }
            val imageMap = mutableMapOf<Int, MutableMap<String, Any>>()

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "data") {
                            createRequest = Json.decodeFromString<BadUserCreateRequest>(part.value)
                        }
                    }
                    is PartData.FileItem -> {
                        val name = part.name ?: ""
                        // Read bytes immediately
                        val bytes = part.provider().toInputStream().use { it.readBytes() }
                        
                        if (name.startsWith("image_")) {
                            val idx = name.removePrefix("image_").toIntOrNull()
                            if (idx != null) {
                                val meta = mapOf(
                                    "bytes" to bytes,
                                    "fileName" to (part.originalFileName ?: "image.jpg"),
                                    "contentType" to (part.contentType?.toString() ?: "image/jpeg")
                                )
                                imageMap.getOrPut(idx) { mutableMapOf() }["original"] = meta
                            }
                        } else if (name.startsWith("blur_image_")) {
                            val idx = name.removePrefix("blur_image_").toIntOrNull()
                            if (idx != null) {
                                imageMap.getOrPut(idx) { mutableMapOf() }["blur"] = bytes
                            }
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (createRequest == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "데이터가 누락되었습니다."))
                return@post
            }

            // Upload processed images
            val uploadedImages = mutableListOf<ImageService.ImageUploadResult>()
            imageMap.keys.sorted().forEach { idx ->
                val data = imageMap[idx]!!
                val original = data["original"] as? Map<String, Any>
                if (original != null) {
                    val bytes = original["bytes"] as ByteArray
                    val fileName = original["fileName"] as String
                    val contentType = original["contentType"] as String
                    val blurBytes = data["blur"] as? ByteArray

                    val result = imageService.uploadImageWithThumbnail(bytes, fileName, contentType, blurBytes)
                    uploadedImages.add(result)
                }
            }

            if (uploadedImages.size > 20) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "사진은 최대 20장까지만 첨부할 수 있습니다."))
                return@post
            }

            try {
                // User object for service
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
            // Map to store paired images
            val imageMap = mutableMapOf<Int, MutableMap<String, Any>>()
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
                        val name = part.name ?: ""
                        val bytes = part.provider().toInputStream().use { it.readBytes() }
                        
                        if (name.startsWith("image_")) {
                            val idx = name.removePrefix("image_").toIntOrNull()
                            if (idx != null) {
                                val meta = mapOf(
                                    "bytes" to bytes,
                                    "fileName" to (part.originalFileName ?: "image.jpg"),
                                    "contentType" to (part.contentType?.toString() ?: "image/jpeg")
                                )
                                imageMap.getOrPut(idx) { mutableMapOf() }["original"] = meta
                            }
                        } else if (name.startsWith("blur_image_")) {
                            val idx = name.removePrefix("blur_image_").toIntOrNull()
                            if (idx != null) {
                                imageMap.getOrPut(idx) { mutableMapOf() }["blur"] = bytes
                            }
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (updateRequest == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "데이터가 누락되었습니다."))
                return@put
            }

            // Upload collected images
            val newImages = mutableListOf<ImageService.ImageUploadResult>()
            imageMap.keys.sorted().forEach { idx ->
                val data = imageMap[idx]!!
                val original = data["original"] as? Map<String, Any>
                if (original != null) {
                    val bytes = original["bytes"] as ByteArray
                    val fileName = original["fileName"] as String
                    val contentType = original["contentType"] as String
                    val blurBytes = data["blur"] as? ByteArray

                    val result = imageService.uploadImageWithThumbnail(bytes, fileName, contentType, blurBytes)
                    newImages.add(result)
                }
            }

            try {
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
