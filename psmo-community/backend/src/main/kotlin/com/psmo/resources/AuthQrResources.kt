package com.psmo.resources

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("qr")
class Qr(val parent: Api.Auth = Api.Auth()) {
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
