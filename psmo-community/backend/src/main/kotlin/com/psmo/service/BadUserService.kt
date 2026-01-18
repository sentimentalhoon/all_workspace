package com.psmo.service

import com.psmo.model.User
import com.psmo.model.dto.BadUserCreateRequest
import com.psmo.model.dto.BadUserResponse
import com.psmo.repository.BadUserRepository
import java.security.MessageDigest

class BadUserService(
    private val badUserRepository: BadUserRepository
) {
    // SHA-256 Hashing for Phone Number
    private fun hashPhoneNumber(phoneNumber: String): String {
        // 숫자만 남기고 제거 (하이픈 등 제거)
        val plain = phoneNumber.replace(Regex("[^0-9]"), "")
        val bytes = plain.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    private fun extractLast4(phoneNumber: String): String {
        val plain = phoneNumber.replace(Regex("[^0-9]"), "")
        return if (plain.length >= 4) plain.takeLast(4) else plain
    }

    suspend fun reportBadUser(reporter: User, request: BadUserCreateRequest, imageUrls: List<String>): BadUserResponse {
        val phoneHash = hashPhoneNumber(request.phoneNumber)
        val phoneLast4 = extractLast4(request.phoneNumber)

        val badUser = badUserRepository.create(reporter, request, phoneHash, phoneLast4, imageUrls)
        return badUser.toResponse()
    }

    suspend fun searchBadUsers(keyword: String?): List<BadUserResponse> {
        // 검색 키워드 전처리 (혹시 전화번호 검색이면 숫자만 남기기 등...은 일단 보류, 이름 검색도 있으니까)
        return badUserRepository.search(keyword).map { it.toResponse() }
    }
}
