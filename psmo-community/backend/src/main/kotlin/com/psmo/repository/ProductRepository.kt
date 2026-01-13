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
            it[title] = request.title
            it[description] = request.description
            it[price] = request.price
            it[category] = request.category
            it[Products.sellerId] = sellerId
        }

        // Insert Real Estate Info if present
        request.realEstate?.let { info ->
            ProductRealEstateInfos.insert {
                it[productId] = id
                it[locationCity] = info.locationCity
                it[locationDistrict] = info.locationDistrict
                it[pcCount] = info.pcCount
                it[deposit] = info.deposit
                it[monthlyRent] = info.monthlyRent
                it[managementFee] = info.managementFee
                it[averageMonthlyRevenue] = info.averageMonthlyRevenue
                it[floor] = info.floor
                it[areaMeters] = info.areaMeters
            }
        }

        // Return full object
        findById(id.value)!!.toProduct()
    }

    fun findAll(page: Int, size: Int, category: ProductCategory?): List<ResultRow> = transaction(database) {
        // Left Join to include optional Real Estate info
        val query = (Products innerJoin Users leftJoin ProductRealEstateInfos).selectAll()
        
        category?.let {
            query.andWhere { Products.category eq it }
        }

        query.orderBy(Products.createdAt to SortOrder.DESC)
            .limit(size, offset = ((page - 1) * size).toLong())
            .toList()
    }

    fun findById(id: Long): ResultRow? = transaction(database) {
        (Products innerJoin Users leftJoin ProductRealEstateInfos).selectAll()
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
                 it[this.productId] = productId
                 it[this.url] = url
                 it[this.type] = type
                 it[orderIndex] = index
             }
        }
    }
    
    fun update(id: Long, request: ProductUpdateRequest): Boolean = transaction(database) {
        val updated = Products.update({ Products.id eq id }) { stmt ->
            request.title?.let { stmt[title] = it }
            request.description?.let { stmt[description] = it }
            request.price?.let { stmt[price] = it }
            request.category?.let { stmt[category] = it }
            request.status?.let { stmt[status] = it }
        } > 0
        
        request.realEstate?.let { info ->
            // Check if exists, update or insert
            val existing = ProductRealEstateInfos.selectAll().andWhere { ProductRealEstateInfos.productId eq id }.count() > 0
            if (existing) {
                 ProductRealEstateInfos.update({ ProductRealEstateInfos.productId eq id }) {
                    it[locationCity] = info.locationCity
                    it[locationDistrict] = info.locationDistrict
                    it[pcCount] = info.pcCount
                    it[deposit] = info.deposit
                    it[monthlyRent] = info.monthlyRent
                    it[managementFee] = info.managementFee
                    it[averageMonthlyRevenue] = info.averageMonthlyRevenue
                    it[floor] = info.floor
                    it[areaMeters] = info.areaMeters
                 }
            } else {
                ProductRealEstateInfos.insert {
                    it[productId] = id
                    it[locationCity] = info.locationCity
                    it[locationDistrict] = info.locationDistrict
                    it[pcCount] = info.pcCount
                    it[deposit] = info.deposit
                    it[monthlyRent] = info.monthlyRent
                    it[managementFee] = info.managementFee
                    it[averageMonthlyRevenue] = info.averageMonthlyRevenue
                    it[floor] = info.floor
                    it[areaMeters] = info.areaMeters
                }
            }
        }
        
        updated
    }
}
