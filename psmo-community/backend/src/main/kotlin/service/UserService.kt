package com.psmo.service

import com.psmo.database.DatabaseConfig
import com.psmo.model.User
import com.psmo.model.UserRole
import com.psmo.model.Users
import com.psmo.model.toUser
import io.ktor.server.config.ApplicationConfig
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * Telegram 기반 사용자 CRUD 를 담당한다.
 * Flyway 마이그레이션으로 테이블이 선행 생성된다는 가정 하에 동작한다.
 */
class UserService(
    private val config: ApplicationConfig
) {
    private val database by lazy { DatabaseConfig.connectToDatabase(config) }

    /**
     * Telegram 로그인 정보로 사용자를 upsert 한다.
     * TODO: Telegram 외 OAuth 공급자가 추가되면 provider column 으로 분기하도록 스키마 확장
     */
    fun upsertTelegramUser(
        telegramId: Long,
        firstName: String?,
        lastName: String?,
        username: String?,
        photoUrl: String?
    ): User = transaction(database) {
        val displayName = buildDisplayName(firstName, lastName, username)
        val now = LocalDateTime.now()
        val existing = Users.selectAll()
            .andWhere { Users.telegramId eq telegramId }
            .singleOrNull()

        if (existing == null) {
            Users.insert {
                it[Users.telegramId] = telegramId
                it[Users.displayName] = displayName
                it[Users.username] = username
                it[Users.photoUrl] = photoUrl
                it[Users.role] = UserRole.MEMBER
                it[Users.score] = 0
                it[Users.activityLevel] = 1
                it[Users.createdAt] = now
                it[Users.updatedAt] = now
            }
        } else {
            Users.update({ Users.telegramId eq telegramId }) {
                it[Users.displayName] = displayName
                it[Users.username] = username
                it[Users.photoUrl] = photoUrl
                it[Users.updatedAt] = now
            }
        }

        Users.selectAll()
            .andWhere { Users.telegramId eq telegramId }
            .single()
            .toUser()
    }

    /**
     * 사용자 기본키를 통해 프로필을 조회한다.
     */
    fun getUserById(id: Long): User? = transaction(database) {
        Users.selectAll()
            .andWhere { Users.id eq id }
            .singleOrNull()
            ?.toUser()
    }

    /**
     * 점수를 증감시키고 활동 레벨을 재계산한다.
     */
    fun adjustScore(userId: Long, delta: Int): User? = transaction(database) {
        val user = Users.selectAll().andWhere { Users.id eq userId }.singleOrNull() ?: return@transaction null
        val currentScore = user[Users.score]
        val newScore = (currentScore + delta).coerceAtLeast(0)
        val newLevel = ActivityLevelPolicy.levelForScore(newScore)

        Users.update({ Users.id eq userId }) {
            it[score] = newScore
            it[activityLevel] = newLevel
            it[updatedAt] = LocalDateTime.now()
        }

        Users.selectAll().andWhere { Users.id eq userId }.single().toUser()
    }

    /**
     * Telegram 제공 정보 중 우선순위를 정해 표시명으로 사용한다.
     */
    private fun buildDisplayName(firstName: String?, lastName: String?, username: String?): String? {
        val combined = listOfNotNull(firstName?.takeIf { it.isNotBlank() }, lastName?.takeIf { it.isNotBlank() })
            .joinToString(" ")
            .takeIf { it.isNotBlank() }

        return combined ?: username?.takeIf { it.isNotBlank() }
    }
}
