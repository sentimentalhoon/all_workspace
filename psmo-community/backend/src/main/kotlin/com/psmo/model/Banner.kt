package com.psmo.model

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.CurrentDateTime

object Banners : LongIdTable("banners") {
    val title = varchar("title", 255)
    val imageUrl = varchar("image_url", 512)
    val linkUrl = varchar("link_url", 512).nullable()
    val isVisible = bool("is_visible").default(true)
    val orderIndex = integer("order_index").default(0)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}

class Banner(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Banner>(Banners)

    var title by Banners.title
    var imageUrl by Banners.imageUrl
    var linkUrl by Banners.linkUrl
    var isVisible by Banners.isVisible
    var orderIndex by Banners.orderIndex
    var createdAt by Banners.createdAt
}
