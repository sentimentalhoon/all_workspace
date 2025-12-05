package com.psmo.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.psmo.database.DatabaseConfig
import com.psmo.database.RedisConfig
import com.psmo.model.ChatMessage
import com.psmo.model.ChatMessages
import com.psmo.model.User
import com.psmo.model.dto.ChatBroadcastMessage
import com.psmo.model.dto.toBroadcast
import com.psmo.model.toChatMessage
import com.psmo.model.Users
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.tryGetString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import redis.clients.jedis.JedisPubSub
import java.time.LocalDateTime

/**
 * 채팅 메시지 저장 및 Redis Pub/Sub 브로커 연동을 담당.
 */
class ChatService(
    private val config: ApplicationConfig
) {
    private val database by lazy { DatabaseConfig.connectToDatabase(config) }
    private val jedisPool by lazy { RedisConfig.createJedisPool(config) }
    private val redisChannel = config.tryGetString("chat.redisChannel") ?: "psmo:chat:global"
    private val historyLimit = config.tryGetString("chat.historyLimit")?.toIntOrNull() ?: 50
    private val objectMapper = jacksonObjectMapper()

    fun appendMessage(user: User, content: String): ChatBroadcastMessage = transaction(database) {
        val body = content.trim()
        require(body.isNotBlank()) { "Message content cannot be blank" }
        val now = LocalDateTime.now()

        val messageId = ChatMessages.insertAndGetId { row ->
            row[ChatMessages.userId] = EntityID(user.id, Users)
            row[ChatMessages.displayName] = user.displayName
            row[ChatMessages.username] = user.username
            row[ChatMessages.content] = body
            row[ChatMessages.createdAt] = now
        }

        ChatMessage(
            id = messageId.value,
            userId = user.id,
            displayName = user.displayName,
            username = user.username,
            content = body,
            createdAt = now
        ).toBroadcast()
    }

    fun recentMessages(limit: Int = historyLimit): List<ChatBroadcastMessage> = transaction(database) {
        ChatMessages.selectAll()
            .orderBy(ChatMessages.createdAt, SortOrder.DESC)
            .limit(limit)
            .map { it.toChatMessage() }
            .sortedBy { it.createdAt }
            .map { it.toBroadcast() }
    }

    fun publish(message: ChatBroadcastMessage) {
        val payload = objectMapper.writeValueAsString(message)
        jedisPool.resource.use { jedis ->
            jedis.publish(redisChannel, payload)
        }
    }

    fun subscribe(scope: CoroutineScope, onMessage: (ChatBroadcastMessage) -> Unit) {
        scope.launch(Dispatchers.IO) {
            var keepRunning = true
            while (keepRunning && scope.isActive) {
                jedisPool.resource.use { jedis ->
                    val subscriber = object : JedisPubSub() {
                        override fun onMessage(channel: String, message: String) {
                            val payload = runCatching {
                                objectMapper.readValue<ChatBroadcastMessage>(message)
                            }.getOrElse { return }
                            scope.launch { onMessage(payload) }
                        }
                    }
                    try {
                        jedis.subscribe(subscriber, redisChannel)
                    } catch (_: Exception) {
                        if (!scope.isActive) {
                            subscriber.unsubscribe()
                            keepRunning = false
                            return@use
                        }
                    }
                }
            }
        }
    }
}
