package com.psmo.model

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object TestDataTable : LongIdTable("test_data") {
    val name = varchar("name", 100)
    val value = text("value")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}

data class TestDataDTO(
    val id: Long? = null,
    val name: String,
    val value: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
