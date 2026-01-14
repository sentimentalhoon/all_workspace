package com.psmo.repository

import com.psmo.database.DatabaseConfig
import com.psmo.model.Product
import com.psmo.model.ProductCategory
import com.psmo.model.Products
import com.psmo.model.ProductRealEstateInfos
import com.psmo.model.ProductImages
import com.psmo.model.Users
import com.psmo.model.dto.ProductCreateRequest
import com.psmo.model.dto.ProductUpdateRequest
import com.psmo.model.toProduct
import io.ktor.server.config.ApplicationConfig
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class ProductRepository(private val config: ApplicationConfig) {
    private val database by lazy { DatabaseConfig.connectToDatabase(config) }

    fun create(request: ProductCreateRequest, sellerId: Long): Product = transaction(database) {
        val id = Products.insertAndGetId {
            it[Products.title] = request.title
            it[Products.description] = request.description
            it[Products.price] = request.price
            it[Products.category] = request.category
            it[Products.sellerId] = sellerId
        }

        // Insert Real Estate Info if present
        // Insert Real Estate Info if present
        request.realEstate?.let { info ->
            ProductRealEstateInfos.insert {
                it[ProductRealEstateInfos.id] = id.value // Shared PK
                it[ProductRealEstateInfos.locationCity] = info.locationCity
                it[ProductRealEstateInfos.locationDistrict] = info.locationDistrict
                it[ProductRealEstateInfos.pcCount] = info.pcCount
                it[ProductRealEstateInfos.deposit] = info.deposit
                it[ProductRealEstateInfos.monthlyRent] = info.monthlyRent
                it[ProductRealEstateInfos.managementFee] = info.managementFee
                it[ProductRealEstateInfos.averageMonthlyRevenue] = info.averageMonthlyRevenue
                it[ProductRealEstateInfos.floor] = info.floor
                it[ProductRealEstateInfos.areaMeters] = info.areaMeters
            }
        }

        // Return full object
        findById(id.value)!!.toProduct()
    }

    fun findAll(page: Int, size: Int, category: ProductCategory?): List<ResultRow> = transaction(database) {
        // Left Join to include optional Real Estate info using specific constraint
        val query = (Products innerJoin Users).leftJoin(ProductRealEstateInfos) { Products.id eq ProductRealEstateInfos.id }.selectAll()
        
        category?.let {
            query.andWhere { Products.category eq it }
        }

        query.orderBy(Products.createdAt to SortOrder.DESC)
            .limit(size, offset = ((page - 1) * size).toLong())
            .toList()
    }

    fun findById(id: Long): ResultRow? = transaction(database) {
        (Products innerJoin Users).leftJoin(ProductRealEstateInfos) { Products.id eq ProductRealEstateInfos.id }.selectAll()
            .andWhere { Products.id eq id }
            .singleOrNull()
    }

    fun getImages(productId: Long): List<ResultRow> = transaction(database) {
        ProductImages.selectAll()
            .andWhere { ProductImages.productId eq productId }
            .orderBy(ProductImages.orderIndex to SortOrder.ASC)
            .toList()
    }
    
    fun saveImages(productId: Long, images: List<Pair<String, com.psmo.model.ProductMediaType>>) = transaction(database) {
        // Clear existing (if this becomes a full replacement strategy, otherwise append)
        // For simplicity in create, just append. For update, might need logic.
        // Assuming append for now or handled by service.
        images.forEachIndexed { index, (url, type) -> 
             ProductImages.insert {
                 it[ProductImages.productId] = productId
                 it[ProductImages.url] = url
                 it[ProductImages.type] = type
                 it[ProductImages.orderIndex] = index
             }
        }
    }
    
    fun update(id: Long, request: ProductUpdateRequest): Boolean = transaction(database) {
        val updated = Products.update({ Products.id eq id }) { stmt ->
            request.title?.let { stmt[Products.title] = it }
            request.description?.let { stmt[Products.description] = it }
            request.price?.let { stmt[Products.price] = it }
            request.category?.let { stmt[Products.category] = it }
            request.status?.let { stmt[Products.status] = it }
        } > 0
        
        request.realEstate?.let { info ->
            // Check if exists, update or insert
            val existing = ProductRealEstateInfos.selectAll().andWhere { ProductRealEstateInfos.id eq id }.count() > 0
            if (existing) {
                 ProductRealEstateInfos.update({ ProductRealEstateInfos.id eq id }) {
                    it[ProductRealEstateInfos.locationCity] = info.locationCity
                    it[ProductRealEstateInfos.locationDistrict] = info.locationDistrict
                    it[ProductRealEstateInfos.pcCount] = info.pcCount
                    it[ProductRealEstateInfos.deposit] = info.deposit
                    it[ProductRealEstateInfos.monthlyRent] = info.monthlyRent
                    it[ProductRealEstateInfos.managementFee] = info.managementFee
                    it[ProductRealEstateInfos.averageMonthlyRevenue] = info.averageMonthlyRevenue
                    it[ProductRealEstateInfos.floor] = info.floor
                    it[ProductRealEstateInfos.areaMeters] = info.areaMeters
                 }
            } else {
                ProductRealEstateInfos.insert {
                    it[ProductRealEstateInfos.id] = id
                    it[ProductRealEstateInfos.locationCity] = info.locationCity
                    it[ProductRealEstateInfos.locationDistrict] = info.locationDistrict
                    it[ProductRealEstateInfos.pcCount] = info.pcCount
                    it[ProductRealEstateInfos.deposit] = info.deposit
                    it[ProductRealEstateInfos.monthlyRent] = info.monthlyRent
                    it[ProductRealEstateInfos.managementFee] = info.managementFee
                    it[ProductRealEstateInfos.averageMonthlyRevenue] = info.averageMonthlyRevenue
                    it[ProductRealEstateInfos.floor] = info.floor
                    it[ProductRealEstateInfos.areaMeters] = info.areaMeters
                }
            }
        }
        
        updated
    }
}
