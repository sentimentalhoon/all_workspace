package com.psmo.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.tryGetString
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource

/**
 * PostgreSQL(HikariCP) 연결을 생성하고 Exposed Database 객체를 제공한다.
 */
object DatabaseConfig {
    private data class DbSettings(
        val jdbcUrl: String,
        val username: String,
        val password: String,
        val driverClassName: String,
        val maxPoolSize: Int,
        val minIdle: Int
    ) {
        val migrationKey: String = "$jdbcUrl::$username"
    }

    private val migrationStates = ConcurrentHashMap<String, Boolean>()

    /**
     * HikariCP 커넥션 풀을 구성한다.
     * TODO: 운영 환경에서 반드시 비밀 정보는 Secret Manager 로부터 주입
     */
    fun createDataSource(config: ApplicationConfig): DataSource =
        createDataSource(loadSettings(config))

    private fun loadSettings(config: ApplicationConfig): DbSettings {
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

        return DbSettings(
            jdbcUrl = jdbcUrl,
            username = username,
            password = password,
            driverClassName = driverClassName,
            maxPoolSize = maxPoolSize,
            minIdle = minIdle
        )
    }

    private fun createDataSource(settings: DbSettings): DataSource =
        HikariDataSource(
            HikariConfig().apply {
                jdbcUrl = settings.jdbcUrl
                username = settings.username
                password = settings.password
                driverClassName = settings.driverClassName
                maximumPoolSize = settings.maxPoolSize
                minimumIdle = settings.minIdle
                idleTimeout = 600_000
                connectionTimeout = 30_000
                validationTimeout = 5_000
            }
        )

    /**
     * Exposed Database 객체를 생성한다.
     */
    fun connectToDatabase(config: ApplicationConfig): Database {
        val settings = loadSettings(config)
        val dataSource = createDataSource(settings)
        runMigrations(dataSource, settings.migrationKey)
        return Database.connect(dataSource)
    }

    private fun runMigrations(dataSource: DataSource, key: String) {
        migrationStates.computeIfAbsent(key) {
            val flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .sqlMigrationPrefix("V")
                .repeatableSqlMigrationPrefix("R")
                .sqlMigrationSeparator("__")
                .sqlMigrationSuffixes(".sql")
                .validateMigrationNaming(true)
                .load()

            flyway.migrate()
            true
        }
    }
}
