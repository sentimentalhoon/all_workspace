package com.psmo.service

import com.psmo.model.dto.ProductCreateRequest
import com.psmo.model.dto.ProductResponse
import com.psmo.model.dto.ProductUpdateRequest
import com.psmo.model.dto.toResponse
import com.psmo.model.toProduct
import com.psmo.model.toUser
import com.psmo.repository.ProductRepository
import java.time.format.DateTimeFormatter

/**
 * 상품 관련 비즈니스 로직을 처리하는 곳입니다.
 * (컨트롤러와 리포지토리 사이의 중간 다리)
 *
 * 예: "상품을 등록한다"라고 하면,
 * 1. 리포지토리에게 데이터를 저장하라고 시키고
 * 2. 이미지가 있으면 이미지도 저장하고
 * 3. 저장된 결과를 예쁘게 포장해서 돌려줍니다.
 */
class ProductService(private val repository: ProductRepository) {

    suspend fun createProduct(request: ProductCreateRequest, sellerId: Long, images: List<Pair<com.psmo.service.ImageService.ImageUploadResult, com.psmo.model.ProductMediaType>> = emptyList()): ProductResponse {
        val product = repository.create(request, sellerId)
        
        // Save Images
        if (images.isNotEmpty()) {
            repository.saveImages(product.id, images)
        }
        return getProductById(product.id) ?: throw IllegalStateException("Created product not found")
    }

    /**
     * 상품 목록을 가져와서 화면에 보여줄 수 있는 형태(DTO)로 변환합니다.
     */
    suspend fun getProducts(page: Int, size: Int, category: String?): List<ProductResponse> {
        val categoryEnum = category?.let { 
            try { com.psmo.model.ProductCategory.valueOf(it) } catch (e: Exception) { null } 
        }
        
        return repository.findAll(page, size, categoryEnum).map { row ->
            val product = row.toProduct()
            val user = row.toUser()
            // 각 상품마다 이미지들을 따로 가져옵니다. (나중에 최적화 필요)
            val images = repository.getImages(product.id).map { 
                com.psmo.model.dto.ProductImageDto(
                    it[com.psmo.model.ProductImages.id].value,
                    it[com.psmo.model.ProductImages.url],
                    it[com.psmo.model.ProductImages.thumbnailUrl],
                    it[com.psmo.model.ProductImages.type],
                    it[com.psmo.model.ProductImages.orderIndex]
                )
            }
            
            // 모든 정보를 합쳐서 응답 객체(ProductResponse)로 만듭니다.
            product.copy(
                images = images.map { com.psmo.model.ProductImage(it.id, it.url, it.thumbnailUrl, it.type, it.orderIndex) }
                // RealEstate populated by join in repo if present in Product object
            ).toResponse(user.toResponse())
        }
    }

    suspend fun getProductById(id: Long): ProductResponse? {
        val row = repository.findById(id) ?: return null
        val product = row.toProduct()
        val user = row.toUser()
        
        val imagesDto = repository.getImages(product.id).map { 
             com.psmo.model.dto.ProductImageDto(
                it[com.psmo.model.ProductImages.id].value,
                it[com.psmo.model.ProductImages.url],
                it[com.psmo.model.ProductImages.thumbnailUrl],
                it[com.psmo.model.ProductImages.type],
                it[com.psmo.model.ProductImages.orderIndex]
            )
        }

        return product.copy(
            images = imagesDto.map { com.psmo.model.ProductImage(it.id, it.url, it.thumbnailUrl, it.type, it.orderIndex) }
        ).toResponse(user.toResponse())
    }
    
    suspend fun updateProduct(
        id: Long, 
        request: ProductUpdateRequest, 
        userId: Long, 
        isAdmin: Boolean = false,
        newImages: List<Pair<com.psmo.service.ImageService.ImageUploadResult, com.psmo.model.ProductMediaType>> = emptyList(),
        deleteImageIds: List<Long> = emptyList()
    ): ProductResponse? {
        val existing = getProductById(id) ?: return null
        
        // Owner Check bypassed if Admin
        if (existing.seller.id != userId && !isAdmin) {
            throw IllegalArgumentException("Not the owner of this product")
        }
        
        // Handle Images
        if (deleteImageIds.isNotEmpty()) {
            repository.deleteImages(deleteImageIds)
        }
        if (newImages.isNotEmpty()) {
            repository.saveImages(id, newImages)
        }
        
        repository.update(id, request)
        return getProductById(id)
    }
    suspend fun deleteProduct(id: Long, userId: Long, isAdmin: Boolean = false): Boolean {
        val existing = getProductById(id) ?: return false
        if (existing.seller.id != userId && !isAdmin) {
            throw IllegalArgumentException("Not the owner of this product")
        }
        return repository.delete(id)
    }

    suspend fun changeStatus(id: Long, status: com.psmo.model.ProductStatus): Boolean {
        return repository.updateStatus(id, status)
    }
}
