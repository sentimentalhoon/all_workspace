package com.psmo.controller

import com.psmo.model.dto.ProductCreateRequest
import com.psmo.model.dto.ProductUpdateRequest
import com.psmo.service.ProductService
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
        class Id(val parent: Products = Products(), val id: Long)
    }
}

fun Route.productRoutes(service: ProductService) {


    get<MarketResources.Products> { params ->
        val products = service.getProducts(params.page, params.size, params.category)
        call.respond(mapOf("status" to "success", "data" to products))
    }

    // 상품 상세 조회 (누구나 볼 수 있음)
    get<MarketResources.Products.Id> { params ->
        val product = service.getProductById(params.id)
        if (product == null) {
            call.respond(HttpStatusCode.NotFound, mapOf("status" to "error", "message" to "Product not found"))
        } else {
            call.respond(mapOf("status" to "success", "data" to product))
        }
    }

    authenticate("auth-jwt") {
        // 상품 등록 (로그인 필요)
        post<MarketResources.Products> {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("id").asLong()

            // Handle Multipart (파일 업로드 처리)
            // 상품 정보(JSON)와 사진 파일(File)이 섞여서 들어오기 때문에 복잡한 처리가 필요합니다.
            var productRequest: ProductCreateRequest? = null
            val uploadedImages = mutableListOf<Pair<String, com.psmo.model.ProductMediaType>>()
            
            // Inject services and mapper once
            val imageService by inject<com.psmo.service.ImageService>()
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
                        val fileName = part.originalFileName as String
                        val contentType = part.contentType?.toString() ?: "application/octet-stream"
                        
                        // Use provider directly for efficiency (Streaming)
                        // MinIO SDK is blocking, so offload to IO dispatcher
                        val url = withContext(Dispatchers.IO) {
                            part.provider().use { inputStream ->
                                val type = if (contentType.startsWith("video")) com.psmo.model.ProductMediaType.VIDEO else com.psmo.model.ProductMediaType.IMAGE
                                val uploadedUrl = if (type == com.psmo.model.ProductMediaType.VIDEO) {
                                    imageService.uploadVideo(inputStream, fileName, contentType)
                                } else {
                                    imageService.uploadImage(inputStream, fileName, contentType)
                                }
                                uploadedUrl to type
                            }
                        }
                        uploadedImages.add(url)
                    }
                    else -> {}
                }
                part.dispose()
            }
            
            if (productRequest == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "error", "message" to "Missing product data"))
                return@post
            }

            val created = service.createProduct(productRequest!!, userId, uploadedImages)
            call.respond(HttpStatusCode.Created, mapOf("status" to "success", "data" to created))
        }
        
        put<MarketResources.Products.Id> { params ->
             val request = call.receive<ProductUpdateRequest>()
             val principal = call.principal<JWTPrincipal>()
             val userId = principal!!.payload.getClaim("id").asLong()
             
             try {
                val updated = service.updateProduct(params.id, request, userId)
                if (updated != null) {
                    call.respond(mapOf("status" to "success", "data" to updated))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("status" to "error", "message" to "Product not found or access denied"))
                }
             } catch (e: IllegalArgumentException) {
                 call.respond(HttpStatusCode.Forbidden, mapOf("status" to "error", "message" to e.message))
             }
        }
    }
}
