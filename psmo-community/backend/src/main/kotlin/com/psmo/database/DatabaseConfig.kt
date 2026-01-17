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
 * 데이터베이스(DB)와의 연결을 담당하는 설정 파일입니다.
 *
 * HikariCP라는 기술을 사용해서 DB와 미리 여러 개의 연결(Connection Pool)을 만들어두고,
 * 필요할 때마다 빠르게 빌려다 쓸 수 있게 해줍니다.
 * 마치 도서관에서 책을 미리 꽂아두고 대여해주는 것과 비슷합니다.
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
     * HikariCP 설정 객체를 만듭니다.
     * 여기에는 DB 주소, 아이디, 비밀번호, 그리고 "동시에 최대 몇 명까지 접속할 수 있는지(maxPoolSize)" 같은 정보가 들어갑니다.
     *
     * 주의: 실제 서비스(운영 환경)에서는 비밀번호 같은 중요 정보를 코드에 직접 적으면 안 되고, 암호화해서 관리해야 합니다.
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
     * 실제로 데이터베이스에 접속을 시도하고, 'Exposed'라는 도구가 DB를 쓸 수 있도록 준비해줍니다.
     *
     * 또한, `runMigrations`를 호출해서 DB 테이블이 없으면 자동으로 만들어줍니다(Flyway).
     * 이것은 마치 "이사 갈 집에 가구가 없으면, 자동으로 가구를 배치해주는 로봇"과 같습니다.
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

            flyway.repair()
            flyway.migrate()
            true
        }
    }
}
