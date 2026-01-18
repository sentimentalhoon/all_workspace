package com.psmo.model

import com.psmo.model.dto.BadUserResponse
import com.psmo.model.dto.BadUserImageResponse
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * 불량 사용자 (블랙리스트) 테이블
 * 개인정보 보호를 위해 이름/전화번호 등은 저장하지 않습니다.
 */
object BadUsers : LongIdTable("bad_users") {
    val region = varchar("region", 100)
    val reason = varchar("reason", 2000)
    val physicalDescription = varchar("physical_description", 500).nullable()
    val incidentDate = date("incident_date").nullable()
    val reporter = reference("reporter_id", Users)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}

class BadUser(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<BadUser>(BadUsers)

    var region by BadUsers.region
    var reason by BadUsers.reason
    var physicalDescription by BadUsers.physicalDescription
    var incidentDate by BadUsers.incidentDate
    var reporter by UserEntity referencedOn BadUsers.reporter
    var createdAt by BadUsers.createdAt
    val images by BadUserImage referrersOn BadUserImages.badUser

    fun toResponse() = BadUserResponse(
        id = this.id.value,
        region = this.region,
        reason = this.reason,
        physicalDescription = this.physicalDescription,
        incidentDate = this.incidentDate?.toString(),
        images = this.images.map { BadUserImageResponse(it.url, it.thumbnailUrl) },
        reporterName = this.reporter.displayName ?: "익명",
        reporterId = this.reporter.id.value,
        createdAt = this.createdAt.toString()
    )
}

object BadUserImages : LongIdTable("bad_user_images") {
    val badUser = reference("bad_user_id", BadUsers)
    val url = varchar("url", 500)
    val thumbnailUrl = varchar("thumbnail_url", 500)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}

class BadUserImage(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<BadUserImage>(BadUserImages)

    var badUser by BadUser referencedOn BadUserImages.badUser
    var url by BadUserImages.url
    var thumbnailUrl by BadUserImages.thumbnailUrl
    var createdAt by BadUserImages.createdAt
}
