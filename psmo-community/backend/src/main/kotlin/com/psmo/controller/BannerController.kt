package com.psmo.controller

import com.psmo.model.UserRole
import com.psmo.model.dto.BannerCreateRequest
import com.psmo.model.dto.BannerOrderRequest
import com.psmo.model.dto.BannerUpdateRequest
import com.psmo.service.BannerService
import com.psmo.service.ImageService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Route.bannerRoutes(bannerService: BannerService, imageService: ImageService) {
    route("/api/banners") {
        // Public: Get Visible Banners
        get {
            val banners = bannerService.getBanners(visibleOnly = true)
            call.respond(banners)
        }

        // Admin Only
        authenticate("auth-jwt") {
            route("/admin") {
                // Get All (including hidden)
                get {
                    val principal = call.principal<JWTPrincipal>()
                    val role = principal?.payload?.getClaim("role")?.asString()
                    if (role != UserRole.ADMIN.name) {
                        call.respond(HttpStatusCode.Forbidden, "Admin only")
                        return@get
                    }
                    val banners = bannerService.getBanners(visibleOnly = false)
                    call.respond(banners)
                }
                
                // Create
                post {
                    val principal = call.principal<JWTPrincipal>()
                    val role = principal?.payload?.getClaim("role")?.asString()
                    if (role != UserRole.ADMIN.name) {
                        call.respond(HttpStatusCode.Forbidden, "Admin only")
                        return@post
                    }

                    var title = ""
                    var linkUrl: String? = null
                    var isVisible = true
                    var orderIndex = 0
                    var imageBytes: ByteArray? = null
                    var filename = ""

                    val multipart = call.receiveMultipart()
                    multipart.forEachPart { part ->
                        when(part) {
                            is PartData.FormItem -> {
                                when(part.name) {
                                    "title" -> title = part.value
                                    "linkUrl" -> linkUrl = part.value
                                    "isVisible" -> isVisible = part.value.toBoolean()
                                    "orderIndex" -> orderIndex = part.value.toIntOrNull() ?: 0
                                }
                            }
                            is PartData.FileItem -> {
                                if (part.name == "image") {
                                    filename = part.originalFileName ?: "banner.jpg"
                                    imageBytes = part.streamProvider().readBytes()
                                }
                            }
                            else -> {}
                        }
                        part.dispose()
                    }

                    if (title.isBlank() || imageBytes == null) {
                        call.respond(HttpStatusCode.BadRequest, "Title and Image are required")
                        return@post
                    }

                    val imageResult = imageService.uploadImageWithThumbnail(imageBytes!!, filename, "image/jpeg") // Simple upload
                    // Actually, banner might not need thumb/blur complexity. Just pure upload.
                    // But our ImageService returns ImageUploadResult. Let's stick to consistent pattern/method or simplify.
                    // Let's use uploadImageWithThumbnail but maybe ignore thumb if not passed to entity?
                    // Wait, Banner entity only has `imageUrl`. 
                    // Let's create a simpler upload in ImageService or just use `originalUrl`.

                    val request = BannerCreateRequest(title, linkUrl, isVisible, orderIndex)
                    val response = bannerService.createBanner(request, imageResult)
                    call.respond(response)
                }
                
                // Delete
                delete("/{id}") {
                    val principal = call.principal<JWTPrincipal>()
                    val role = principal?.payload?.getClaim("role")?.asString()
                    if (role != UserRole.ADMIN.name) {
                        call.respond(HttpStatusCode.Forbidden, "Admin only")
                        return@delete
                    }
                    val id = call.parameters["id"]?.toLongOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@delete
                    }
                    bannerService.deleteBanner(id)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}
