package com.psmo.service

import com.psmo.database.DatabaseConfig
import com.psmo.model.User
import com.psmo.model.Users
import com.psmo.model.toUser
import io.ktor.server.config.ApplicationConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class UserService(
    private val config: ApplicationConfig
) {
    private val database by lazy {
        DatabaseConfig.connectToDatabase(config).also { db ->
            transaction(db) {
                SchemaUtils.create(Users)
            }
        }
    }

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

    fun getUserById(id: Long): User? = transaction(database) {
        Users.selectAll()
            .andWhere { Users.id eq id }
            .singleOrNull()
            ?.toUser()
    }

    private fun buildDisplayName(firstName: String?, lastName: String?, username: String?): String? {
        val combined = listOfNotNull(firstName?.takeIf { it.isNotBlank() }, lastName?.takeIf { it.isNotBlank() })
            .joinToString(" ")
            .takeIf { it.isNotBlank() }

        return combined ?: username?.takeIf { it.isNotBlank() }
    }
}
