package com.psmo.resources

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/api/blacklist")
class BadUserListResource(val keyword: String? = null)

@Serializable
@Resource("/api/blacklist")
class BadUserCreateResource {
    @Serializable
    @Resource("{id}")
    class Id(val parent: BadUserCreateResource = BadUserCreateResource(), val id: Long)
}
