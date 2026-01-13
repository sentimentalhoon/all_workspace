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

    get<MarketResources.Products.Id> { params ->
        val product = service.getProductById(params.id)
        if (product == null) {
            call.respond(HttpStatusCode.NotFound, mapOf("status" to "error", "message" to "Product not found"))
        } else {
            call.respond(mapOf("status" to "success", "data" to product))
        }
    }

    authenticate("auth-jwt") {
    authenticate("auth-jwt") {
        post<MarketResources.Products> {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("id").asLong()

            // Handle Multipart
            var productRequest: ProductCreateRequest? = null
            val uploadedImages = mutableListOf<Pair<String, com.psmo.model.ProductMediaType>>()
            
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "product") {
                            // Deserialize JSON
                             val jackson = io.ktor.serialization.jackson.jacksonObjectMapper()
                             productRequest = jackson.readValue(part.value, ProductCreateRequest::class.java)
                        }
                    }
                    is PartData.FileItem -> {
                        val fileName = part.originalFileName as String
                        val contentType = part.contentType?.toString() ?: "application/octet-stream"
                        val bytes = part.streamProvider().readBytes() // Read to memory for simplicity, or stream
                        val inputStream = java.io.ByteArrayInputStream(bytes)
                         
                        // Determine type
                        val type = if (contentType.startsWith("video")) com.psmo.model.ProductMediaType.VIDEO else com.psmo.model.ProductMediaType.IMAGE
                        val imageService by inject<com.psmo.service.ImageService>()
                        
                        // Upload
                        val url = if (type == com.psmo.model.ProductMediaType.VIDEO) {
                            imageService.uploadVideo(inputStream, fileName, contentType)
                        } else {
                            imageService.uploadImage(inputStream, fileName, contentType)
                        }
                        
                        uploadedImages.add(url to type)
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
