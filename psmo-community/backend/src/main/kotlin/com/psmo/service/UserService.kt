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
 * 사용자를 관리하는 역할을 합니다. (회원가입, 조회, 점수 수정 등)
 *
 * 이 서비스는 Flyway로 DB 테이블이 이미 만들어져 있다고 가정하고 일합니다.
 */
class UserService(
    private val config: ApplicationConfig
) {
    private val database by lazy { DatabaseConfig.connectToDatabase(config) }

    /**
     * 텔레그램 로그인을 처리합니다.
     * - 만약 처음 온 사람이면? -> 새로 등록합니다 (Insert)
     * - 이미 가입한 사람이면? -> 정보(이름, 사진 등)를 업데이트합니다 (Update)
     * 이를 합쳐서 UPSERT(Update + Insert)라고 합니다.
     *
     * TODO: 나중에 구글, 카카오 로그인이 추가되면 여기서 분기 처리를 해줘야 합니다.
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
     * 사용자의 점수를 올리거나 내립니다.
     * 점수가 바뀌면 활동 레벨(Activity Level)도 같이 다시 계산해서 저장합니다.
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
