package com.psmo.resources

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/api")
class QrApi(val parent: Api = Api()) {
    @Serializable
    @Resource("auth")
    class Auth(val parent: QrApi = QrApi()) {
        @Serializable
        @Resource("qr")
        class Qr(val parent: Auth = Auth()) {
            @Serializable
            @Resource("init")
            class Init(val parent: Qr = Qr())

            @Serializable
            @Resource("check")
            class Check(val parent: Qr = Qr())

            @Serializable
            @Resource("claim")
            class Claim(val parent: Qr = Qr())
        }
    }
}
