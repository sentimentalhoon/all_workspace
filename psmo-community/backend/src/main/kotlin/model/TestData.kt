package com.psmo.model

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * 진단용 테스트 테이블. 운영 데이터와 분리된 공간을 사용한다.
 */
object TestDataTable : LongIdTable("test_data") {
    val name = varchar("name", 100)
    val value = text("value")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}

/**
 * 테스트 저장/응답을 위한 DTO.
 */
data class TestDataDTO(
    val id: Long? = null,
    val name: String,
    val value: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
