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
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList

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
                this.blurUrl = image.blurUrl
                this.blurThumbnailUrl = image.blurThumbnailUrl
            }
        }

        badUser.toResponse(reporter.id)
    }

    suspend fun search(keyword: String?, viewerId: Long?): List<BadUserResponse> = newSuspendedTransaction(Dispatchers.IO) {
        if (keyword.isNullOrBlank()) {
            return@newSuspendedTransaction BadUser.all()
                .sortedByDescending { it.createdAt }
                .map { it.toResponse(viewerId) }
        }

        // 지역 또는 피해사유로 검색
        BadUser.find {
            (BadUsers.region like "%$keyword%") or (BadUsers.reason like "%$keyword%")
        }.sortedByDescending { it.createdAt }
            .map { it.toResponse(viewerId) }
    }

    suspend fun findById(id: Long, viewerId: Long?): BadUserResponse? = newSuspendedTransaction(Dispatchers.IO) {
        BadUser.findById(id)?.toResponse(viewerId)
    }

    suspend fun update(
        id: Long, 
        request: com.psmo.model.dto.BadUserUpdateRequest,
        newImages: List<com.psmo.service.ImageService.ImageUploadResult>,
        deleteImageIds: List<Long>,
        viewerId: Long?
    ): BadUserResponse? = newSuspendedTransaction(Dispatchers.IO) {
        val badUser = BadUser.findById(id) ?: return@newSuspendedTransaction null

        request.region?.let { badUser.region = it }
        request.reason?.let { badUser.reason = it }
        request.physicalDescription?.let { badUser.physicalDescription = it }
        request.incidentDate?.let { badUser.incidentDate = LocalDate.parse(it) }

        // Save new images
        newImages.forEach { image ->
             BadUserImage.new {
                this.badUser = badUser
                this.url = image.originalUrl
                this.thumbnailUrl = image.thumbnailUrl
                this.blurUrl = image.blurUrl
                this.blurThumbnailUrl = image.blurThumbnailUrl
            }
        }

        // Delete images
        if (deleteImageIds.isNotEmpty()) {
            BadUserImage.find {
                 (com.psmo.model.BadUserImages.id inList deleteImageIds) and (com.psmo.model.BadUserImages.badUser eq id) 
            }.forEach { it.delete() }
        }

        badUser.toResponse(viewerId)
    }

    suspend fun delete(id: Long) = newSuspendedTransaction(Dispatchers.IO) {
        // Cascade delete should handle images if configured, but let's be explicit or rely on Entity delete
        BadUser.findById(id)?.delete()
    }
}
