package com.psmo.resources

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Resource("/api")
class Api {
    @Resource("auth")
    class Auth(val parent: Api = Api()) {
        @Resource("telegram")
        class Telegram(val parent: Auth = Auth())
        
        @Resource("refresh")
        class Refresh(val parent: Auth = Auth())
        
        @Resource("logout")
        class Logout(val parent: Auth = Auth())
    }
    
    @Resource("me")
    class Me(val parent: Api = Api())
}
