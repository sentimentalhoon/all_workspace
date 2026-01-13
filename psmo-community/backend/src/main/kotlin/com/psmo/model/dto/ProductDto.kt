package com.psmo.model.dto

import com.psmo.model.Product
import com.psmo.model.ProductCategory
import com.psmo.model.ProductStatus
import com.psmo.model.ProductRealEstateInfo
import com.psmo.model.ProductImage
import com.psmo.model.ProductMediaType
import java.io.Serializable
import java.time.LocalDateTime

data class ProductCreateRequest(
    val title: String,
    val description: String?,
    val price: Int,
    val category: ProductCategory,
    // Real Estate Info (Optional)
    val realEstate: ProductRealEstateDto? = null
)

data class ProductRealEstateDto(
    val locationCity: String,
    val locationDistrict: String,
    val pcCount: Int,
    val deposit: Long,
    val monthlyRent: Int,
    val managementFee: Int,
    val averageMonthlyRevenue: Long,
    val floor: Int?,
    val areaMeters: Float?
)

data class ProductImageDto(
    val id: Long,
    val url: String,
    val type: ProductMediaType,
    val orderIndex: Int
)

data class ProductResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val price: Int,
    val status: ProductStatus,
    val category: ProductCategory,
    val sellerId: Long,
    val viewCount: Int,
    val createdAt: String,
    val updatedAt: String,
    // Extensions
    val realEstate: ProductRealEstateDto? = null,
    val images: List<ProductImageDto> = emptyList()
) : Serializable

data class ProductUpdateRequest(
    val title: String?,
    val description: String?,
    val price: Int?,
    val status: ProductStatus?,
    val category: ProductCategory?,
    val realEstate: ProductRealEstateDto? = null
)

fun Product.toResponse(): ProductResponse {
    return ProductResponse(
        id = this.id,
        title = this.title,
        description = this.description,
        price = this.price,
        status = this.status,
        category = this.category,
        sellerId = this.sellerId,
        viewCount = this.viewCount,
        createdAt = this.createdAt.toString(),
        updatedAt = this.updatedAt.toString(),
        realEstate = this.realEstateInfo?.let { 
             ProductRealEstateDto(
                 it.locationCity, it.locationDistrict, it.pcCount, it.deposit, 
                 it.monthlyRent, it.managementFee, it.averageMonthlyRevenue, 
                 it.floor, it.areaMeters
             )
        },
        images = this.images.map {
            ProductImageDto(it.id, it.url, it.type, it.orderIndex)
        }
    )
}
