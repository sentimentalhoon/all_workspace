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

/**
 * 상품 관련 데이터를 직접 DB에 저장하거나 꺼내오는 역할을 합니다.
 * (DAO/Repository 패턴)
 */
class ProductRepository(private val config: ApplicationConfig) {
    private val database by lazy { DatabaseConfig.connectToDatabase(config) }

    /**
     * 상품을 새로 등록합니다.
     * transaction 블록: "이 작업들은 모두 성공하거나, 하나라도 실패하면 시도조차 안 한 것으로 하겠다"는 약속입니다.
     */
    fun create(request: ProductCreateRequest, sellerId: Long): Product = transaction(database) {
        val id = Products.insertAndGetId {
            it[Products.title] = request.title
            it[Products.description] = request.description
            it[Products.price] = request.price
            it[Products.category] = request.category
            it[Products.sellerId] = sellerId
        }

        // 부동산 정보가 있으면 같이 저장합니다.
        request.realEstate?.let { info ->
            ProductRealEstateInfos.insert {
                it[ProductRealEstateInfos.id] = id.value // 상품 ID와 똑같은 ID를 씁니다. (1:1 관계)
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

        // 방금 만든 상품 정보를 다시 읽어서 반환합니다.
        findById(id.value)!!.toProduct()
    }

    /**
     * 상품 목록을 조회합니다. (페이지네이션 기능 포함)
     * page: 몇 번째 페이지인지
     * size: 한 번에 몇 개씩 보여줄지
     * category: 특정 카테고리만 보고 싶을 때 (없으면 전체 조회)
     */
    fun findAll(page: Int, size: Int, category: ProductCategory?): List<ResultRow> = transaction(database) {
        // 상품 테이블(Products)과 사용자 테이블(Users)을 연결(Inner Join)해서 판매자 정보도 같이 가져옵니다.
        // 부동산 정보(RealEstate)는 있을 수도 있고 없을 수도 있어서 Left Join을 사용합니다.
        val query = (Products innerJoin Users).leftJoin(ProductRealEstateInfos) { Products.id eq ProductRealEstateInfos.id }.selectAll()
        
        category?.let {
            query.andWhere { Products.category eq it }
        }

        // 최신순(내림차순)으로 정렬하고 필요한 개수만 잘라서 가져옵니다.
        query.orderBy(Products.createdAt to SortOrder.DESC)
            .limit(size, offset = ((page - 1) * size).toLong())
            .toList()
    }

    fun findById(id: Long): ResultRow? = transaction(database) {
        (Products innerJoin Users).leftJoin(ProductRealEstateInfos) { Products.id eq ProductRealEstateInfos.id }.selectAll()
            .andWhere { Products.id eq id }
            .singleOrNull()
    }

    /**
     * 특정 상품의 이미지 목록을 가져옵니다.
     */
    fun getImages(productId: Long): List<ResultRow> = transaction(database) {
        ProductImages.selectAll()
            .andWhere { ProductImages.productId eq productId }
            .orderBy(ProductImages.orderIndex to SortOrder.ASC) // 순서대로 정렬
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
