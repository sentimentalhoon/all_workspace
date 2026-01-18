package com.psmo.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class BadUserCreateRequest(
    val name: String,
    val phoneNumber: String, // 입력받은 전체 전화번호 (저장 시 해시/마스킹 처리)
    val birthYear: Int? = null,
    val reason: String,
)

@Serializable
data class BadUserResponse(
    val id: Long,
    val name: String,
    val phoneLast4: String, // 보안을 위해 뒷 4자리만 반환
    val birthYear: Int?,
    val reason: String,
    val imageUrls: List<String>,
    val reporterName: String,
    val createdAt: String,
    // 검증 로직은 별도 API로 분리하거나 클라이언트 해시 비교 (여기선 단순 조회용)
)

@Serializable
data class BadUserImageDto(
    val id: Long,
    val url: String
)
