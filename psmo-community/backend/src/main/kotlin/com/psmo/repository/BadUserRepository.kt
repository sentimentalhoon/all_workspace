package com.psmo.repository

import com.psmo.model.BadUser
import com.psmo.model.BadUserImage
import com.psmo.model.BadUsers
import com.psmo.model.User
import com.psmo.model.UserEntity
import com.psmo.model.dto.BadUserCreateRequest
import com.psmo.model.dto.BadUserResponse
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDate

class BadUserRepository {

    suspend fun create(
        reporter: User,
        request: BadUserCreateRequest,
        images: List<com.psmo.service.ImageService.ImageUploadResult>
    ): BadUserResponse = newSuspendedTransaction(Dispatchers.IO) {
        val badUser = BadUser.new {
            this.region = request.region
            this.reason = request.reason
            this.physicalDescription = request.physicalDescription
            this.incidentDate = request.incidentDate?.let { LocalDate.parse(it) }
            this.reporter = UserEntity.findById(reporter.id)
                ?: throw IllegalArgumentException("Reporter user not found")
        }

        images.forEach { image ->
            BadUserImage.new {
                this.badUser = badUser
                this.url = image.originalUrl
                this.thumbnailUrl = image.thumbnailUrl
            }
        }

        badUser.toResponse()
    }

    suspend fun search(keyword: String?): List<BadUserResponse> = newSuspendedTransaction(Dispatchers.IO) {
        if (keyword.isNullOrBlank()) {
            return@newSuspendedTransaction BadUser.all()
                .sortedByDescending { it.createdAt }
                .map { it.toResponse() }
        }

        // 지역 또는 피해사유로 검색
        BadUser.find {
            (BadUsers.region like "%$keyword%") or (BadUsers.reason like "%$keyword%")
        }.sortedByDescending { it.createdAt }
            .map { it.toResponse() }
    }
}
