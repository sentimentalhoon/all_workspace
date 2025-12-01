package com.psmo.database

import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.tryGetString
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

object RedisConfig {
    fun createJedisPool(config: ApplicationConfig): JedisPool {
        val host = config.tryGetString("redis.host")
            ?: System.getenv("REDIS_HOST")
            ?: "psmo-redis"
        val port = config.tryGetString("redis.port")?.toIntOrNull()
            ?: System.getenv("REDIS_PORT")?.toIntOrNull()
            ?: 6379
        val password = config.tryGetString("redis.password")
            ?: System.getenv("REDIS_PASSWORD")
            ?: "psmo_dev_redis"

        return JedisPool(
            JedisPoolConfig().apply {
                maxTotal = 10
                maxIdle = 5
                minIdle = 1
                testOnBorrow = true
                testOnReturn = true
                testWhileIdle = true
            },
            host,
            port,
            2000,
            password
        )
    }
}
