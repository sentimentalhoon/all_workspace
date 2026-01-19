package com.psmo.repository

import com.psmo.model.Banner
import com.psmo.model.Banners
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class BannerRepository {
    suspend fun findAll(): List<Banner> = newSuspendedTransaction(Dispatchers.IO) {
        Banner.all().sortedBy { it.orderIndex }.toList()
    }

    suspend fun findVisible(): List<Banner> = newSuspendedTransaction(Dispatchers.IO) {
        Banner.find { Banners.isVisible eq true }
            .sortedBy { it.orderIndex }
            .toList()
    }

    suspend fun create(title: String, imageUrl: String, linkUrl: String?, isVisible: Boolean, orderIndex: Int): Banner = newSuspendedTransaction(Dispatchers.IO) {
        Banner.new {
            this.title = title
            this.imageUrl = imageUrl
            this.linkUrl = linkUrl
            this.isVisible = isVisible
            this.orderIndex = orderIndex
        }
    }

    suspend fun update(id: Long, title: String?, imageUrl: String?, linkUrl: String?, isVisible: Boolean?, orderIndex: Int?): Banner? = newSuspendedTransaction(Dispatchers.IO) {
        val banner = Banner.findById(id) ?: return@newSuspendedTransaction null
        title?.let { banner.title = it }
        imageUrl?.let { banner.imageUrl = it }
        linkUrl?.let { banner.linkUrl = it }
        isVisible?.let { banner.isVisible = it }
        orderIndex?.let { banner.orderIndex = it }
        banner
    }

    suspend fun delete(id: Long): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        val banner = Banner.findById(id) ?: return@newSuspendedTransaction false
        banner.delete()
        true
    }
    
    suspend fun updateOrder(id: Long, newOrder: Int) = newSuspendedTransaction(Dispatchers.IO) {
        val banner = Banner.findById(id)
        banner?.orderIndex = newOrder
    }
}
