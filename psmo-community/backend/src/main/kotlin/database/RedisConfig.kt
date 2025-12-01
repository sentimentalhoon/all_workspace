package com.psmo.database

import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

object RedisConfig {
    fun createJedisPool(
        host: String = System.getenv("REDIS_HOST") ?: "psmo-redis",
        port: Int = System.getenv("REDIS_PORT")?.toIntOrNull() ?: 6379,
        password: String? = System.getenv("REDIS_PASSWORD") ?: "psmo_dev_redis"
    ): JedisPool = JedisPool(
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
