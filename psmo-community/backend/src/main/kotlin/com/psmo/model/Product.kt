package com.psmo.model

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

enum class ProductStatus {
    SALE, RESERVED, SOLD
}

enum class ProductCategory {
    PC_FULL, CPU, GPU, RAM, MAINBOARD, SSD_HDD, CASE, POWER, MONITOR, GEAR, SOFTWARE, ETC,
    PC_BUSINESS // PC방 매매
}

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
    val floor = integer("floor").nullable()
    val areaMeters = float("area_meters").nullable()
}

enum class ProductMediaType { IMAGE, VIDEO }

object ProductImages : LongIdTable("product_images") {
    val productId = reference("product_id", Products)
    val url = varchar("url", 512)
    val type = enumerationByName("type", 20, ProductMediaType::class)
    val orderIndex = integer("order_index").default(0)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}

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
    val floor: Int?,
    val areaMeters: Float?
)

data class ProductImage(
    val id: Long,
    val url: String,
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
    realEstateInfo = if (this.hasValue(ProductRealEstateInfos.pcCount)) { // Check if joined column exists/not null
        // Exposed's nullable join handling is tricky. If leftJoin returned nulls, fields are null.
        // But pcCount is integer().default(0), so checking ID or nullable column is better.
        // Let's check if the ID column of the joined table is present and not null
        // But Products table also has ID. 
        // We can check a mandatory column from RealEstate.
        try {
             if (this[ProductRealEstateInfos.locationCity] != null) {
                 ProductRealEstateInfo(
                     this[ProductRealEstateInfos.locationCity],
                     this[ProductRealEstateInfos.locationDistrict],
                     this[ProductRealEstateInfos.pcCount],
                     this[ProductRealEstateInfos.deposit],
                     this[ProductRealEstateInfos.monthlyRent],
                     this[ProductRealEstateInfos.managementFee],
                     this[ProductRealEstateInfos.averageMonthlyRevenue],
                     this[ProductRealEstateInfos.floor],
                     this[ProductRealEstateInfos.areaMeters]
                 )
             } else null
        } catch(e: Exception) { null } // Column might not be in result set if not joined
    } else null
)
