package com.psmo.config

import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.config.ApplicationConfig

/**
 * 활성 profile 과 병합된 설정 객체를 함께 들고 다니기 위한 DTO.
 */
data class ActiveProfileConfig(
    val profile: String,
    val config: ApplicationConfig
)

/**
 * KTOR_PROFILE/KTOR_ENV/ktor.environment 값을 읽어 활성 프로파일을 결정하고, 현 config 를 그대로 반환한다.
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
