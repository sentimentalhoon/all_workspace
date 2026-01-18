package com.psmo.model

import com.psmo.model.dto.BadUserResponse
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * 불량 사용자 (블랙리스트) 테이블
 * 개인정보 보호를 위해 전화번호는 전체 저장하지 않고,
 * 해시값(검증용)과 뒷 4자리(조회/표시용)만 저장합니다.
 */
object BadUsers : LongIdTable("bad_users") {
    val name = varchar("name", 50)
    val phoneHash = varchar("phone_hash", 64) // SHA-256 Hash for exact match verification
    val phoneLast4 = varchar("phone_last4", 4) // Last 4 digits for display/search
    val birthYear = integer("birth_year").nullable()
    val reason = varchar("reason", 500)
    val reporter = reference("reporter_id", Users)
    val createdAt = datetime("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentDateTime)
}

class BadUser(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<BadUser>(BadUsers)

    var name by BadUsers.name
    var phoneHash by BadUsers.phoneHash
    var phoneLast4 by BadUsers.phoneLast4
    var birthYear by BadUsers.birthYear
    var reason by BadUsers.reason
    var reporter by UserEntity referencedOn BadUsers.reporter
    var createdAt by BadUsers.createdAt
    val images by BadUserImage referrersOn BadUserImages.badUser

    fun toResponse() = BadUserResponse(
        id = this.id.value,
        name = this.name,
        phoneLast4 = this.phoneLast4,
        birthYear = this.birthYear,
        reason = this.reason,
        imageUrls = this.images.map { it.url },
        reporterName = this.reporter.displayName ?: "Unknown",
        createdAt = this.createdAt.toString()
    )
}

object BadUserImages : LongIdTable("bad_user_images") {
    val badUser = reference("bad_user_id", BadUsers)
    val url = varchar("url", 255)
    val createdAt = datetime("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentDateTime)
}

class BadUserImage(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<BadUserImage>(BadUserImages)

    var badUser by BadUser referencedOn BadUserImages.badUser
    var url by BadUserImages.url
    var createdAt by BadUserImages.createdAt
}
