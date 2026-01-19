package com.psmo.model.dto

import com.psmo.model.Product
import com.psmo.model.ProductCategory
import com.psmo.model.ProductStatus
import com.psmo.model.ProductRealEstateInfo
import com.psmo.model.ProductImage
import com.psmo.model.ProductMediaType
import java.io.Serializable
import java.time.LocalDateTime

/**
 * 상품을 등록(Create)할 때 클라이언트(프론트엔드)에서 보내주는 데이터 양식(Request DTO)입니다.
 * 제목, 설명, 가격, 카테고리는 필수입니다.
 */
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
    val rightsMoney: Long,
    val floor: Int?,
    val areaMeters: Float?,
    val areaPyeong: Float?,
    val facilities: String?,
    val moveInDate: String?,
    val permitStatus: String?,
    val adminActionHistory: String?,
    val contactNumber: String?
)

data class ProductImageDto(
    val id: Long,
    val imageUrl: String,
    val thumbnailUrl: String,
    val type: ProductMediaType,
    val orderIndex: Int
)

/**
 * 클라이언트에게 상품 정보를 보여줄 때 사용하는 응답 양식(Response DTO)입니다.
 * DB에 있는 모든 정보를 다 보여주는 게 아니라, 보여줘도 되는 정보만 골라서 담습니다.
 *
 * seller: 판매자 정보도 포함해서 보여줍니다.
 */
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
    val updatedAt: String,
    // Extensions
    val realEstate: ProductRealEstateDto? = null,
    val images: List<ProductImageDto> = emptyList()
) : Serializable

/**
 * 상품 정보를 수정(Update)할 때 사용하는 양식입니다.
 * 모든 필드가 다 있을 필요가 없어서, 수정하고 싶은 항목만 채워서 보낼 수 있게 물음표(?)가 붙어있습니다(Nullable).
 */
data class ProductUpdateRequest(
    val title: String?,
    val description: String?,
    val price: Int?,
    val status: ProductStatus?,
    val category: ProductCategory?,
    val realEstate: ProductRealEstateDto? = null
)

fun Product.toResponse(seller: UserResponse, viewerId: Long?): ProductResponse {
    val showOriginal = viewerId != null // Logged-in users see original
    
    return ProductResponse(
        id = this.id,
        title = this.title,
        description = this.description,
        price = this.price,
        status = this.status,
        category = this.category,
        seller = seller,
        viewCount = this.viewCount,
        createdAt = this.createdAt.toString(),
        updatedAt = this.updatedAt.toString(),
        realEstate = this.realEstateInfo?.let { 
             ProductRealEstateDto(
                 it.locationCity, it.locationDistrict, it.pcCount, it.deposit, 
                 it.monthlyRent, it.managementFee, it.averageMonthlyRevenue, 
                 it.rightsMoney, it.floor, it.areaMeters,
                 it.areaPyeong, it.facilities, it.moveInDate,
                 it.permitStatus, it.adminActionHistory, it.contactNumber
             )
        },
        images = this.images.map {
            val finalUrl = if (showOriginal) it.url else (it.blurUrl ?: it.url)
            val finalThumb = if (showOriginal) it.thumbnailUrl else (it.blurThumbnailUrl ?: it.thumbnailUrl)
            
            ProductImageDto(it.id, finalUrl, finalThumb, it.type, it.orderIndex)
        }
    )
}

data class ProductStatusUpdateRequest(
    val status: ProductStatus
)
