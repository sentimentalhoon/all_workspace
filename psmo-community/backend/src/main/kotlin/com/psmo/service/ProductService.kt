package com.psmo.service

import com.psmo.model.dto.ProductCreateRequest
import com.psmo.model.dto.ProductResponse
import com.psmo.model.dto.ProductUpdateRequest
import com.psmo.model.dto.toResponse
import com.psmo.model.toProduct
import com.psmo.model.toUser
import com.psmo.repository.ProductRepository
import java.time.format.DateTimeFormatter

class ProductService(private val repository: ProductRepository) {

    fun createProduct(request: ProductCreateRequest, sellerId: Long): ProductResponse {
        val product = repository.create(request, sellerId)
        // Note: product.sellerId is Long. 
        // We need ResultRow to get full user info easily, or fetch User separately.
        // In Repository.create, we fetched (Products innerJoin Users), so the result Product 
        // DOES NOT contain the User object deeply. 
        // However, I made repository.create return Product.
        // Let's refactor Repository to return ResultRow or simpler:
        // Just fetch the result row by ID again.
        
        // Actually, to keep it efficient, create return the Product ID, and then we fetch.
        // But for now, let's just fetch by ID.
        return getProductById(product.id) ?: throw IllegalStateException("Created product not found")
    }

    fun getProducts(page: Int, size: Int, category: String?): List<ProductResponse> {
        val categoryEnum = category?.let { 
            try { com.psmo.model.ProductCategory.valueOf(it) } catch (e: Exception) { null } 
        }
        
        return repository.findAll(page, size, categoryEnum).map { row ->
            val product = row.toProduct()
            val user = row.toUser()
            
            ProductResponse(
                id = product.id,
                title = product.title,
                description = product.description,
                price = product.price,
                status = product.status,
                category = product.category,
                seller = user.toResponse(),
                viewCount = product.viewCount,
                createdAt = product.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
                updatedAt = product.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME)
            )
        }
    }

    fun getProductById(id: Long): ProductResponse? {
        val row = repository.findById(id) ?: return null
        val product = row.toProduct()
        val user = row.toUser()
        
        return ProductResponse(
             id = product.id,
                title = product.title,
                description = product.description,
                price = product.price,
                status = product.status,
                category = product.category,
                seller = user.toResponse(),
                viewCount = product.viewCount,
                createdAt = product.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
                updatedAt = product.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME)
        )
    }
    
    fun updateProduct(id: Long, request: ProductUpdateRequest, userId: Long): ProductResponse? {
        val existing = getProductById(id) ?: return null
        if (existing.seller.id != userId) {
            throw IllegalArgumentException("Not the owner of this product")
        }
        
        repository.update(id, request)
        return getProductById(id)
    }
}
