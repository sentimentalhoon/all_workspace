package com.psmo.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

object DatabaseConfig {
    fun createDataSource(
        jdbcUrl: String = System.getenv("DB_URL") ?: "jdbc:postgresql://psmo-postgres:5432/psmo_dev",
        username: String = System.getenv("DB_USER") ?: "psmo",
        password: String = System.getenv("DB_PASSWORD") ?: "psmo_dev_password"
    ): DataSource = HikariDataSource(HikariConfig().apply {
        this.jdbcUrl = jdbcUrl
        this.username = username
        this.password = password
        driverClassName = "org.postgresql.Driver"
        maximumPoolSize = 10
        minimumIdle = 2
        idleTimeout = 600000
        connectionTimeout = 30000
        validationTimeout = 5000
    })

    fun connectToDatabase(dataSource: DataSource = createDataSource()): Database =
        Database.connect(dataSource)
}
