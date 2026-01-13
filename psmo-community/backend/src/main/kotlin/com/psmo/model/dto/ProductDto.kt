package com.psmo.model.dto

import com.psmo.model.Product
import com.psmo.model.ProductCategory
import com.psmo.model.ProductStatus
import com.psmo.model.User
import java.time.LocalDateTime

data class ProductCreateRequest(
    val title: String,
    val description: String?,
    val price: Int,
    val category: ProductCategory
)

data class ProductResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val price: Int,
    val status: ProductStatus,
    val category: ProductCategory,
    val seller: UserResponse,
    val viewCount: Int,
    val createdAt: String,
    val updatedAt: String
)

data class ProductUpdateRequest(
    val title: String?,
    val description: String?,
    val price: Int?,
    val status: ProductStatus?,
    val category: ProductCategory?
)
