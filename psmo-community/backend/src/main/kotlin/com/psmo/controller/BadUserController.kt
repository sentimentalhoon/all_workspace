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
    authenticate("auth-jwt") {
        post<BadUserCreateResource> {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.subject?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "인증 필요"))

            // Multipart Request 처리
            val multipart = call.receiveMultipart()
            var createRequest: BadUserCreateRequest? = null
            val uploadedImageUrls = mutableListOf<String>()

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "data") {
                            createRequest = Json.decodeFromString<BadUserCreateRequest>(part.value)
                        }
                    }
                    is PartData.FileItem -> {
                        // MinIO에 업로드
                        val fileName = part.originalFileName ?: "image.jpg"
                        val contentType = part.contentType?.toString() ?: "image/jpeg"
                        
                        val url = withContext(Dispatchers.IO) {
                            part.provider().toInputStream().use { inputStream ->
                                imageService.uploadImage(inputStream, fileName, contentType)
                            }
                        }
                        uploadedImageUrls.add(url)
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (createRequest == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "데이터가 누락되었습니다."))
                return@post
            }

            // 사진 최대 20장 제한
            if (uploadedImageUrls.size > 20) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "사진은 최대 20장까지만 첨부할 수 있습니다."))
                return@post
            }

            try {
                // User 객체 생성 (Service에서 ID만 사용)
                val now = java.time.LocalDateTime.now()
                val user = User(
                    id = userId, 
                    telegramId = 0, 
                    displayName = null,
                    username = null,
                    photoUrl = null,
                    role = com.psmo.model.UserRole.MEMBER,
                    score = 0,
                    activityLevel = 0,
                    createdAt = now,
                    updatedAt = now
                )
                val response = badUserService.reportBadUser(user, createRequest!!, uploadedImageUrls)
                call.respond(HttpStatusCode.Created, response)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "잘못된 요청")))
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "서버 오류가 발생했습니다."))
            }
        }
    }
}
