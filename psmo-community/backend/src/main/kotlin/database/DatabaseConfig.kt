package com.psmo.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.tryGetString
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

object DatabaseConfig {
    fun createDataSource(config: ApplicationConfig): DataSource {
        val jdbcUrl = config.tryGetString("database.url")
            ?: System.getenv("DATABASE_URL")
            ?: "jdbc:postgresql://psmo-postgres:5432/psmo_dev"
        val username = config.tryGetString("database.user")
            ?: config.tryGetString("database.username")
            ?: System.getenv("DATABASE_USERNAME")
            ?: System.getenv("DATABASE_USER")
            ?: "psmo"
        val password = config.tryGetString("database.password")
            ?: System.getenv("DATABASE_PASSWORD")
            ?: "psmo_dev_password"
        val driverClassName = config.tryGetString("database.driver") ?: "org.postgresql.Driver"
        val maxPoolSize = config.tryGetString("database.maxPoolSize")?.toIntOrNull() ?: 10
        val minIdle = config.tryGetString("database.minIdle")?.toIntOrNull() ?: 2

        return HikariDataSource(
            HikariConfig().apply {
                this.jdbcUrl = jdbcUrl
                this.username = username
                this.password = password
                this.driverClassName = driverClassName
                maximumPoolSize = maxPoolSize
                minimumIdle = minIdle
                idleTimeout = 600_000
                connectionTimeout = 30_000
                validationTimeout = 5_000
            }
        )
    }

    fun connectToDatabase(config: ApplicationConfig): Database =
        Database.connect(createDataSource(config))
}
