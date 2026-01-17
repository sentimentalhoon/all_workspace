package com.psmo.resources

import io.ktor.resources.*
import kotlinx.serialization.Serializable

/**
 * 인증(Auth) 관련 API 주소를 정의합니다.
 *
 * 구조:
 * - /api
 *   - /auth
 *     - /telegram (텔레그램 로그인)
 *     - /refresh (토큰 갱신)
 *     - /logout (로그아웃)
 *   - /me (내 정보 조회)
 */
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
