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

    suspend fun getBadUserById(id: Long): BadUserResponse {
        return badUserRepository.findById(id) ?: throw IllegalArgumentException("존재하지 않는 게시글입니다.")
    }

    suspend fun updateBadUser(
        id: Long,
        updater: User,
        request: com.psmo.model.dto.BadUserUpdateRequest,
        newImages: List<ImageService.ImageUploadResult>,
        deleteImageIds: List<Long>
    ): BadUserResponse {
        val existing = getBadUserById(id)
        if (existing.reporterId != updater.id && updater.role != com.psmo.model.UserRole.ADMIN) {
            throw IllegalArgumentException("수정 권한이 없습니다.")
        }
        return badUserRepository.update(id, request, newImages, deleteImageIds)
            ?: throw IllegalArgumentException("게시글 수정 실패")
    }

    suspend fun deleteBadUser(id: Long, deleter: User) {
        val existing = getBadUserById(id)
        if (existing.reporterId != deleter.id && deleter.role != com.psmo.model.UserRole.ADMIN) {
            throw IllegalArgumentException("삭제 권한이 없습니다.")
        }
        badUserRepository.delete(id)
    }
}
