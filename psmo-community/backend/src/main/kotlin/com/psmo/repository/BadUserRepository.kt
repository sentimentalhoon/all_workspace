package com.psmo.repository

import com.psmo.model.BadUser
import com.psmo.model.BadUserImage
import com.psmo.model.BadUsers
import com.psmo.model.User
import com.psmo.model.dto.BadUserCreateRequest
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class BadUserRepository {

    suspend fun create(reporter: User, request: BadUserCreateRequest, phoneHash: String, phoneLast4: String, imageUrls: List<String>): BadUser = newSuspendedTransaction(Dispatchers.IO) {
        val badUser = BadUser.new {
            this.name = request.name
            this.phoneHash = phoneHash
            this.phoneLast4 = phoneLast4
            this.birthYear = request.birthYear
            this.reason = request.reason
            this.reporter = reporter
        }

        imageUrls.forEach { url ->
            BadUserImage.new {
                this.badUser = badUser
                this.url = url
            }
        }

        badUser
    }

    suspend fun search(keyword: String?): List<BadUser> = newSuspendedTransaction(Dispatchers.IO) {
        if (keyword.isNullOrBlank()) {
            return@newSuspendedTransaction BadUser.all().sortedByDescending { it.createdAt }.toList()
        }

        // 이름으로 검색하거나 전화번호 뒷 4자리로 검색
        BadUser.find {
            (BadUsers.name like "%$keyword%") or (BadUsers.phoneLast4 like "%$keyword%")
        }.sortedByDescending { it.createdAt }.toList()
    }
}
