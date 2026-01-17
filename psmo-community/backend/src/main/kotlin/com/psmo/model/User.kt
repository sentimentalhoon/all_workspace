package com.psmo.model

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * 시스템 역할 정의. 필요에 따라 추가 가능.
 */
/**
 * 사용자 역할(권한)을 정의하는 곳입니다.
 *
 * SYSTEM: 시스템 관리용 (가장 높은 권한)
 * ADMIN: 전체 관리자
 * MANAGER: 중간 관리자
 * MEMBER: 일반 회원
 */
enum class UserRole {
    SYSTEM,
    ADMIN,
    MANAGER,
    MODERATOR,
    MEMBER
}

/**
 * Telegram 사용자 테이블 정의.
 */
/**
 * 데이터베이스의 'users' 테이블을 코드로 표현한 것입니다.
 * (Exposed 프레임워크 사용)
 *
 * telegram_id: 텔레그램 고유 ID (로그인할 때 사용)
 * score: 활동 점수
 * activity_level: 활동 레벨
 */
object Users : LongIdTable("users") {
    val telegramId = long("telegram_id").uniqueIndex()
    val displayName = varchar("display_name", 150).nullable()
    val username = varchar("username", 150).nullable()
    val photoUrl = varchar("photo_url", 512).nullable()
    val role = enumerationByName("role", 20, UserRole::class)
        .default(UserRole.MEMBER)
    val score = integer("score").default(0)
    val activityLevel = integer("activity_level").default(1)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}

/**
 * 사용자 도메인 모델.
 * TODO: 역할/권한 필드(role 등)를 추가하여 RBAC 구현
 */
/**
 * 실제 프로그램 안에서 사용할 '사용자 객체(User)'입니다.
 * DB 테이블(Users)에서 데이터를 꺼내와서 이 객체에 담아서 사용합니다.
 */
data class User(
    val id: Long,
    val telegramId: Long,
    val displayName: String?,
    val username: String?,
    val photoUrl: String?,
    val role: UserRole,
    val score: Int,
    val activityLevel: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

/**
 * Exposed ResultRow -> User 변환 헬퍼.
 */
fun ResultRow.toUser(): User = User(
    id = this[Users.id].value,
    telegramId = this[Users.telegramId],
    displayName = this[Users.displayName],
    username = this[Users.username],
    photoUrl = this[Users.photoUrl],
    role = this[Users.role],
    score = this[Users.score],
    activityLevel = this[Users.activityLevel],
    createdAt = this[Users.createdAt],
    updatedAt = this[Users.updatedAt]
)
