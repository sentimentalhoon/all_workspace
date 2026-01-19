package com.psmo.service

import com.psmo.model.dto.BannerCreateRequest
import com.psmo.model.dto.BannerResponse
import com.psmo.model.dto.BannerUpdateRequest
import com.psmo.repository.BannerRepository

class BannerService(
    private val repository: BannerRepository,
    private val imageService: ImageService
) {
    suspend fun getBanners(visibleOnly: Boolean = true): List<BannerResponse> {
        val banners = if (visibleOnly) repository.findVisible() else repository.findAll()
        return banners.map { 
            BannerResponse(
                it.id.value, it.title, it.imageUrl, it.linkUrl, it.isVisible, it.orderIndex, it.createdAt.toString()
            )
        }
    }

    suspend fun createBanner(
        request: BannerCreateRequest,
        imageResult: ImageService.ImageUploadResult
    ): BannerResponse {
        val banner = repository.create(
            request.title,
            imageResult.originalUrl, // Use original or thumbnail? Banner usually original
            request.linkUrl,
            request.isVisible,
            request.orderIndex
        )
        return BannerResponse(
            banner.id.value, banner.title, banner.imageUrl, banner.linkUrl, banner.isVisible, banner.orderIndex, banner.createdAt.toString()
        )
    }

    suspend fun updateBanner(
        id: Long,
        request: BannerUpdateRequest,
        newImage: ImageService.ImageUploadResult?
    ): BannerResponse {
        val current = repository.update(
            id,
            request.title,
            newImage?.originalUrl,
            request.linkUrl,
            request.isVisible,
            request.orderIndex
        ) ?: throw IllegalArgumentException("Banner not found")
        
        // If new image uploaded, should we delete old one? 
        // Ideally yes, but for now let's keep it simple or user might revert.
        // Hard deletion policy was for User/Product. Let's stick to safe defaults here or implement if easy.
        
        return BannerResponse(
            current.id.value, current.title, current.imageUrl, current.linkUrl, current.isVisible, current.orderIndex, current.createdAt.toString()
        )
    }

    suspend fun deleteBanner(id: Long) {
        // Fetch to get image URL for deletion
        val banners = repository.findAll() // inefficient but ok for small list
        val target = banners.find { it.id.value == id }
        
        if (target != null) {
            imageService.deleteImage(target.imageUrl)
            repository.delete(id)
        }
    }
    
    suspend fun reorderBanners(orders: Map<Long, Int>) {
        orders.forEach { (id, order) ->
            repository.updateOrder(id, order)
        }
    }
}
