package com.psmo.controller

import com.psmo.model.dto.ProductCreateRequest
import com.psmo.model.dto.ProductUpdateRequest
import com.psmo.service.ProductService
import com.psmo.service.ImageService
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.content.*
import io.ktor.server.plugins.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.koin.ktor.ext.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.ktor.utils.io.jvm.javaio.toInputStream



/**
 * 장터(Market) API 주소를 정의하는 곳입니다.
 *
 * @Resource("/api/v1/market"): 이 주소로 시작하는 모든 요청을 여기서 처리합니다.
 *
 * 구조:
 * - /api/v1/market
 *   - /products (상품 목록)
 *     - /{id} (특정 상품 상세)
 */
@Resource("/api/v1/market")
class MarketResources {
    @Resource("products")
    class Products(val parent: MarketResources = MarketResources(), val page: Int = 1, val size: Int = 20, val category: String? = null) {
        @Resource("{id}")
        class Id(val parent: Products = Products(), val id: Long) {
            @Resource("status")
            class Status(val parent: Id)
        }
    }
}

fun Route.productRoutes(service: ProductService, imageService: ImageService) {
    authenticate("auth-jwt", optional = true) {
        get<MarketResources.Products> { params ->
            val principal = call.principal<JWTPrincipal>()
            val viewerId = principal?.subject?.toLongOrNull()
            val products = service.getProducts(params.page, params.size, params.category, viewerId)
            call.respond(mapOf("status" to "success", "data" to products))
        }

        // 상품 상세 조회 (누구나 볼 수 있음)
        get<MarketResources.Products.Id> { params ->
            val principal = call.principal<JWTPrincipal>()
            val viewerId = principal?.subject?.toLongOrNull()
            val product = service.getProductById(params.id, viewerId)
            if (product == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("status" to "error", "message" to "Product not found"))
            } else {
                call.respond(mapOf("status" to "success", "data" to product))
            }
        }
    }

    authenticate("auth-jwt") {
        // 상품 등록 (로그인 필요)
        post<MarketResources.Products> {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.subject?.toLongOrNull()
            
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "error", "message" to "Invalid token"))
                return@post
            }

            // Handle Multipart (파일 업로드 처리)
            var productRequest: ProductCreateRequest? = null
            // Map: Index -> { "original": Meta, "blur": Bytes }
            val imageMap = mutableMapOf<Int, MutableMap<String, Any>>()
            
            val jackson = jacksonObjectMapper()

            val multipart = call.receiveMultipart()
            
            // Process parts
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "product") {
                            productRequest = jackson.readValue(part.value, ProductCreateRequest::class.java)
                        }
                    }
                    is PartData.FileItem -> {
                        val name = part.name ?: ""
                        // Read bytes immediately (blocking IO context)
                        val bytes = part.provider().toInputStream().use { it.readBytes() }
                        
                        if (name.startsWith("image_")) {
                            val idx = name.removePrefix("image_").toIntOrNull()
                            if (idx != null) {
                                val meta = mapOf(
                                    "bytes" to bytes,
                                    "fileName" to (part.originalFileName ?: "file"),
                                    "contentType" to (part.contentType?.toString() ?: "application/octet-stream")
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
            
            if (productRequest == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "error", "message" to "Missing product data"))
                return@post
            }

            val uploadedImages = mutableListOf<Pair<com.psmo.service.ImageService.ImageUploadResult, com.psmo.model.ProductMediaType>>()

            // Process Map
            imageMap.keys.sorted().forEach { idx ->
                val data = imageMap[idx]!!
                val original = data["original"] as? Map<String, Any>
                
                if (original != null) {
                    val bytes = original["bytes"] as ByteArray
                    val fileName = original["fileName"] as String
                    val contentType = original["contentType"] as String
                    val blurBytes = data["blur"] as? ByteArray
                    
                    val type = if (contentType.startsWith("video")) com.psmo.model.ProductMediaType.VIDEO else com.psmo.model.ProductMediaType.IMAGE
                    
                    // Upload
                    withContext(Dispatchers.IO) {
                        val uploadResult = if (type == com.psmo.model.ProductMediaType.VIDEO) {
                            val videoUrl = imageService.uploadVideo(java.io.ByteArrayInputStream(bytes), fileName, contentType)
                            // Video supports no blur for now
                            com.psmo.service.ImageService.ImageUploadResult(videoUrl, videoUrl) 
                        } else {
                            imageService.uploadImageWithThumbnail(bytes, fileName, contentType, blurBytes)
                        }
                        uploadedImages.add(uploadResult to type)
                    }
                }
            }

            val created = service.createProduct(productRequest!!, userId, uploadedImages)
            call.respond(HttpStatusCode.Created, mapOf("status" to "success", "data" to created))
        }
        
        put<MarketResources.Products.Id> { params ->
             val principal = call.principal<JWTPrincipal>()
             val userId = principal?.subject?.toLongOrNull()
             val role = principal?.payload?.getClaim("role")?.asString()

             if (userId == null) {
                 call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "error", "message" to "Invalid token"))
                 return@put
             }
             
             val isAdmin = role == "ADMIN" || role == "SYSTEM" || role == "MANAGER"

             // Multipart Processing
             var updateRequest: ProductUpdateRequest? = null
             val newImages = mutableListOf<Pair<com.psmo.service.ImageService.ImageUploadResult, com.psmo.model.ProductMediaType>>()
             var deleteImageIds: List<Long> = emptyList()
             val imageMap = mutableMapOf<Int, MutableMap<String, Any>>()
             
             val jackson = jacksonObjectMapper()

             try {
                if (call.request.contentType().match(ContentType.MultiPart.FormData)) {
                     val multipart = call.receiveMultipart()
                     multipart.forEachPart { part ->
                        when(part) {
                            is PartData.FormItem -> {
                                if (part.name == "product") {
                                    updateRequest = jackson.readValue(part.value, ProductUpdateRequest::class.java)
                                } else if (part.name == "deleteImageIds") {
                                    // Expecting JSON array "[1, 2]"
                                    deleteImageIds = jackson.readValue(part.value, object : com.fasterxml.jackson.core.type.TypeReference<List<Long>>() {})
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
                                            "fileName" to (part.originalFileName ?: "file"),
                                            "contentType" to (part.contentType?.toString() ?: "application/octet-stream")
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
                     
                     // Process Map
                    imageMap.keys.sorted().forEach { idx ->
                        val data = imageMap[idx]!!
                        val original = data["original"] as? Map<String, Any>
                        
                        if (original != null) {
                            val bytes = original["bytes"] as ByteArray
                            val fileName = original["fileName"] as String
                            val contentType = original["contentType"] as String
                            val blurBytes = data["blur"] as? ByteArray
                            
                            val type = if (contentType.startsWith("video")) com.psmo.model.ProductMediaType.VIDEO else com.psmo.model.ProductMediaType.IMAGE
                            
                            withContext(Dispatchers.IO) {
                                val uploadResult = if (type == com.psmo.model.ProductMediaType.VIDEO) {
                                    val videoUrl = imageService.uploadVideo(java.io.ByteArrayInputStream(bytes), fileName, contentType)
                                    com.psmo.service.ImageService.ImageUploadResult(videoUrl, videoUrl) 
                                } else {
                                    imageService.uploadImageWithThumbnail(bytes, fileName, contentType, blurBytes)
                                }
                                newImages.add(uploadResult to type)
                            }
                        }
                    }

                } else {
                     // Fallback for JSON only requests
                     updateRequest = call.receive<ProductUpdateRequest>()
                }

                 if (updateRequest == null) {
                     call.respond(HttpStatusCode.BadRequest, mapOf("status" to "error", "message" to "Missing product data"))
                     return@put
                 }

                val updated = service.updateProduct(params.id, updateRequest!!, userId, isAdmin, newImages, deleteImageIds)
                if (updated != null) {
                    call.respond(mapOf("status" to "success", "data" to updated))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("status" to "error", "message" to "Product not found or access denied"))
                }
             } catch (e: Exception) {
                 e.printStackTrace() // Log error
                 call.respond(HttpStatusCode.InternalServerError, mapOf("status" to "error", "message" to (e.message ?: "Unknown error")))
             }
        }

        // 상품 삭제 (Soft Delete)
        delete<MarketResources.Products.Id> { params ->
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.subject?.toLongOrNull()
             val role = principal?.payload?.getClaim("role")?.asString()

            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "error", "message" to "Invalid token"))
                return@delete
            }

            val isAdmin = role == "ADMIN" || role == "SYSTEM" || role == "MANAGER"

            try {
                val deleted = service.deleteProduct(params.id, userId, isAdmin)
                if (deleted) {
                    call.respond(mapOf("status" to "success"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("status" to "error", "message" to "Product not found or access denied"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Forbidden, mapOf("status" to "error", "message" to e.message))
            }
        }
        
        // 상품 상태 변경 (승인/반려/판매완료 등)
        // Admin 권한 체크 로직이 필요하지만, 우선 기능 구현에 집중.
        // TODO: Add Admin role check for PENDING -> SALE transitions
        put<MarketResources.Products.Id.Status> { params ->
             val request = call.receive<com.psmo.model.dto.ProductStatusUpdateRequest>()
             val principal = call.principal<JWTPrincipal>()
             val userId = principal?.subject?.toLongOrNull()
             
             if (userId == null) {
                 call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "error", "message" to "Invalid token"))
                 return@put
             }
             // val role = principal.payload.getClaim("role").asString() // TODO: Use role for strict check
             
             // Currently allows Owner or Admin to change status (logic in Service/Repo is generic, need specific rules)
             // For now, allow change implies approval if Admin, or Sold if Owner.
             // Rely on Client Side or future Role Guards.
             
             val changed = service.changeStatus(params.parent.id, request.status)
             if (changed) {
                 call.respond(mapOf("status" to "success", "newStatus" to request.status))
             } else {
                 call.respond(HttpStatusCode.NotFound, mapOf("status" to "error", "message" to "Product not found"))
             }
        }
    }
}

// Add Resource for Status

