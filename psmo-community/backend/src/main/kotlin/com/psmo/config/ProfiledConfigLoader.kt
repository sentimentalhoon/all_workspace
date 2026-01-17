package com.psmo.config

import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.config.ApplicationConfig

/**
 * 현재 실행 환경(Profile)과 설정(Config)을 한꺼번에 담아서 다니기 위한 가방(DTO)입니다.
 *
 * profile: "dev"(개발용)인지, "prod"(실제 서비스용)인지 적혀있습니다.
 * config: 실제로 읽어온 설정 파일의 내용이 들어있습니다.
 */
data class ActiveProfileConfig(
    val profile: String,
    val config: ApplicationConfig
)

/**
 * 어떤 환경에서 실행할지 결정하고, 그에 맞는 설정을 불러오는 역할을 합니다.
 *
 * 컴퓨터의 환경 변수(KTOR_PROFILE 등)를 확인해서
 * "아, 지금은 개발 모드구나!" 또는 "운영 모드구나!" 하고 판단합니다.
 * 만약 아무것도 없으면 기본적으로 'dev'(개발 모드)로 동작합니다.
 */
object ProfiledConfigLoader {
    private const val DEFAULT_PROFILE = "dev"

    fun load(environment: ApplicationEnvironment): ActiveProfileConfig {
        val baseConfig = environment.config
        val activeProfile = baseConfig.propertyOrNull("ktor.environment")?.getString()
            ?: System.getenv("KTOR_PROFILE")
            ?: System.getenv("KTOR_ENV")
            ?: DEFAULT_PROFILE
        return ActiveProfileConfig(profile = activeProfile, config = baseConfig)
    }

}
