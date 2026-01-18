package com.psmo.resources

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/api/board")
class BoardResources {
    @Serializable
    @Resource("posts")
    class Posts(val parent: BoardResources = BoardResources(), val page: Int = 1, val size: Int = 20, val category: String? = null) {
        @Serializable
        @Resource("{id}")
        class Id(val parent: Posts = Posts(), val id: Long) {
            @Serializable
            @Resource("comments")
            class Comments(val parent: Id)

            @Serializable
            @Resource("like")
            class Like(val parent: Id)
        }
    }
}
