package com.psmo.resources

import io.ktor.resources.*
import kotlinx.serialization.Serializable

/**
 * QR 코드 로그인 관련 API 주소입니다.
 *
 * 구조:
 * - /api/auth/qr
 *   - /init (QR 코드 생성 요청)
 *   - /check (로그인 성공했는지 확인)
 *   - /claim (로그인 완료 처리)
 */
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
