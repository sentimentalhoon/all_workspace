package com.psmo.service

import com.psmo.model.User
import com.psmo.model.dto.BadUserCreateRequest
import com.psmo.model.dto.BadUserResponse
import com.psmo.repository.BadUserRepository

class BadUserService(
    private val badUserRepository: BadUserRepository
) {
    suspend fun reportBadUser(
        reporter: User,
        request: BadUserCreateRequest,
        images: List<ImageService.ImageUploadResult>
    ): BadUserResponse {
        // Validation
        require(request.region.isNotBlank()) { "활동지역은 필수입니다." }
        require(request.reason.isNotBlank()) { "피해사유는 필수입니다." }
        require(request.reason.length <= 2000) { "피해사유는 2000자 이내여야 합니다." }

        return badUserRepository.create(reporter, request, images)
    }

    suspend fun searchBadUsers(keyword: String?): List<BadUserResponse> {
        return badUserRepository.search(keyword)
    }
}
