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
    PC_FULL, CPU, GPU, RAM, MAINBOARD, SSD_HDD, CASE, POWER, MONITOR, GEAR, SOFTWARE, ETC
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
    val updatedAt: LocalDateTime
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
    updatedAt = this[Products.updatedAt]
)
