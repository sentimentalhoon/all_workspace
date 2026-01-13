package com.psmo.repository

import com.psmo.database.DatabaseConfig
import com.psmo.model.Product
import com.psmo.model.ProductCategory
import com.psmo.model.Products
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

        // Fetch back with seller info (optional, or just return basic)
        // For simplicity, re-fetch or construct manually. 
        // Let's re-fetch to ensure consistency with join logic
        (Products innerJoin Users).selectAll().andWhere { Products.id eq id }.single().toProduct()
    }

    fun findAll(page: Int, size: Int, category: ProductCategory?): List<ResultRow> = transaction(database) {
        val query = (Products innerJoin Users).selectAll()
        
        category?.let {
            query.andWhere { Products.category eq it }
        }

        query.orderBy(Products.createdAt to SortOrder.DESC)
            .limit(size, offset = ((page - 1) * size).toLong())
            .toList()
    }

    fun findById(id: Long): ResultRow? = transaction(database) {
        (Products innerJoin Users).selectAll()
            .andWhere { Products.id eq id }
            .singleOrNull()
    }
    
    fun update(id: Long, request: ProductUpdateRequest): Boolean = transaction(database) {
        Products.update({ Products.id eq id }) { stmt ->
            request.title?.let { stmt[title] = it }
            request.description?.let { stmt[description] = it }
            request.price?.let { stmt[price] = it }
            request.category?.let { stmt[category] = it }
            request.status?.let { stmt[status] = it }
        } > 0
    }
}
