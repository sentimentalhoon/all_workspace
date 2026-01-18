package com.psmo.model

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * 상품의 상태를 나타냅니다.
 * SALE: 판매 중
 * RESERVED: 예약 중
 * SOLD: 판매 완료
 */
enum class ProductStatus {
    SALE, RESERVED, SOLD,
    PENDING, // 승인 대기
    HIDDEN,  // 숨김 (작성자/관리자 직권)
    DELETED  // 삭제됨 (Soft Delete)
}

/**
 * 상품 카테고리 목록입니다.
 * 컴퓨터 부품 위주로 구성되어 있습니다.
 */
enum class ProductCategory {
    PC_FULL, CPU, GPU, RAM, MAINBOARD, SSD_HDD, CASE, POWER, MONITOR, GEAR, SOFTWARE, ETC,
    PC_BUSINESS // PC방 매매
}

/**
 * 'products' 테이블 정의.
 * 장터에 올라온 물건 정보를 저장합니다.
 *
 * seller_id: 판매자(User)의 ID와 연결됩니다(Foreign Key).
 */
object Products : LongIdTable("products") {
    val title = varchar("title", 255)
    val description = text("description").nullable()
    val price = integer("price").default(0)
    val status = enumerationByName("status", 50, ProductStatus::class).default(ProductStatus.SALE)
    val category = enumerationByName("category", 50, ProductCategory::class)
    val sellerId = reference("seller_id", Users)
    val viewCount = integer("view_count").default(0)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}

object ProductRealEstateInfos : LongIdTable("product_real_estate_info", "product_id") {
    // Shared PK with Products
    val locationCity = varchar("location_city", 50)
    val locationDistrict = varchar("location_district", 50)
    val pcCount = integer("pc_count").default(0)
    val deposit = long("deposit").default(0)
    val monthlyRent = integer("monthly_rent").default(0)
    val managementFee = integer("management_fee").default(0)
    val averageMonthlyRevenue = long("average_monthly_revenue").default(0)
    val rightsMoney = long("rights_money").default(0)
    val floor = integer("floor").nullable()
    val areaMeters = float("area_meters").nullable()
    val areaPyeong = float("area_pyeong").nullable()
    val facilities = text("facilities").nullable()
    val moveInDate = varchar("move_in_date", 50).nullable()
    val permitStatus = varchar("permit_status", 50).nullable()
    val adminActionHistory = varchar("admin_action_history", 255).nullable()
    val contactNumber = varchar("contact_number", 20).nullable()
}

enum class ProductMediaType { IMAGE, VIDEO }

object ProductImages : LongIdTable("product_images") {
    val productId = reference("product_id", Products)
    val url = varchar("url", 512)
    val thumbnailUrl = varchar("thumbnail_url", 512)
    val type = enumerationByName("type", 20, ProductMediaType::class)
    val orderIndex = integer("order_index").default(0)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}

/**
 * 상품 도메인 객체(Entity)입니다.
 *
 * realEstateInfo: 만약 PC방 매매인 경우, 부동산 정보가 여기에 담깁니다.
 * images: 상품 이미지 목록
 */
data class Product(
    val id: Long,
    val title: String,
    val description: String?,
    val price: Int,
    val status: ProductStatus,
    val category: ProductCategory,
    val sellerId: Long,
    val viewCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    // Optional details
    val realEstateInfo: ProductRealEstateInfo? = null,
    val images: List<ProductImage> = emptyList()
)

data class ProductRealEstateInfo(
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

data class ProductImage(
    val id: Long,
    val url: String,
    val thumbnailUrl: String,
    val type: ProductMediaType,
    val orderIndex: Int
)

fun ResultRow.toProduct(): Product = Product(
    id = this[Products.id].value,
    title = this[Products.title],
    description = this[Products.description],
    price = this[Products.price],
    status = this[Products.status],
    category = this[Products.category],
    sellerId = this[Products.sellerId].value,
    viewCount = this[Products.viewCount],
    createdAt = this[Products.createdAt],
    updatedAt = this[Products.updatedAt],
    realEstateInfo = if (this.getOrNull(ProductRealEstateInfos.id) != null) {
        ProductRealEstateInfo(
            this[ProductRealEstateInfos.locationCity],
            this[ProductRealEstateInfos.locationDistrict],
            this[ProductRealEstateInfos.pcCount],
            this[ProductRealEstateInfos.deposit],
            this[ProductRealEstateInfos.monthlyRent],
            this[ProductRealEstateInfos.managementFee],
            this[ProductRealEstateInfos.averageMonthlyRevenue],
            this[ProductRealEstateInfos.rightsMoney],
            this[ProductRealEstateInfos.floor],
            this[ProductRealEstateInfos.areaMeters],
            this[ProductRealEstateInfos.areaPyeong],
            this[ProductRealEstateInfos.facilities],
            this[ProductRealEstateInfos.moveInDate],
            this[ProductRealEstateInfos.permitStatus],
            this[ProductRealEstateInfos.adminActionHistory],
            this[ProductRealEstateInfos.contactNumber]
        )
    } else null
)
