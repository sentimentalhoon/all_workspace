package com.psmo.controller

// import com.psmo.config.AUTH_JWT
import com.psmo.model.User
import com.psmo.model.dto.BadUserCreateRequest
import com.psmo.resources.BadUserCreateResource
import com.psmo.resources.BadUserListResource
import com.psmo.service.BadUserService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*

fun Route.badUserRoutes(badUserService: BadUserService) {
    // 조회 (공개? 혹은 로그인 필요? 일단 로그인 필요로 설정)
    authenticate("auth-jwt") {
        get<BadUserListResource> { resource ->
            val result = badUserService.searchBadUsers(resource.keyword)
            call.respond(result)
        }

        post<BadUserCreateResource> {
            val user = call.principal<User>()!!
            
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
                        // 파일 저장 로직 (로컬 스토리지 for simple MVP)
                        // 실제로는 S3 / MinIO 등을 사용해야 함.
                        // 여기서는 'uploads' 폴더에 저장한다고 가정.
                        val fileName = "${UUID.randomUUID()}.${File(part.originalFileName ?: "img.jpg").extension}"
                        val file = File("uploads/$fileName")
                        file.parentFile.mkdirs()
                        file.writeBytes(part.provider().readBytes())
                        // URL 생성 (Nginx 또는 Static serve 필요)
                        uploadedImageUrls.add("/uploads/$fileName") 
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (createRequest == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing 'data' field")
                return@post
            }

            val response = badUserService.reportBadUser(user, createRequest!!, uploadedImageUrls)
            call.respond(response)
        }
    }
}
