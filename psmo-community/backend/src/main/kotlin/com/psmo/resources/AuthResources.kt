package com.psmo.resources

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/api")
class Api {
    @Serializable
    @Resource("auth")
    class Auth(val parent: Api = Api()) {
        @Serializable
        @Resource("telegram")
        class Telegram(val parent: Auth = Auth())
        
        @Serializable
        @Resource("refresh")
        class Refresh(val parent: Auth = Auth())
        
        @Serializable
        @Resource("logout")
        class Logout(val parent: Auth = Auth())
    }
    
    @Serializable
    @Resource("me")
    class Me(val parent: Api = Api())
}
