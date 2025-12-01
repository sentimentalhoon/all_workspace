package com.psmo.config

import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.config.yaml.YamlConfigLoader

data class ActiveProfileConfig(
    val profile: String,
    val config: ApplicationConfig
)

object ProfiledConfigLoader {
    private const val DEFAULT_PROFILE = "dev"

    fun load(environment: ApplicationEnvironment): ActiveProfileConfig {
        val baseConfig = environment.config
        val activeProfile = baseConfig.propertyOrNull("profiles.active")?.getString() ?: DEFAULT_PROFILE
        val resourceName = "application-$activeProfile.yaml"
        val profileConfig = YamlConfigLoader().load(resourceName)
            ?: throw IllegalStateException("설정 파일 ${resourceName}을(를) 찾을 수 없습니다.")
        val mergedConfig = mergeConfigs(baseConfig, profileConfig)
        return ActiveProfileConfig(profile = activeProfile, config = mergedConfig)
    }

    private fun mergeConfigs(base: ApplicationConfig, override: ApplicationConfig): ApplicationConfig {
        val mergedEntries = LinkedHashMap<String, String>()
        flattenConfig(base).forEach { (key, value) -> mergedEntries.putIfAbsent(key, value) }
        flattenConfig(override).forEach { (key, value) -> mergedEntries[key] = value }
        return MapApplicationConfig(mergedEntries.map { it.key to it.value })
    }

    private fun flattenConfig(config: ApplicationConfig): Sequence<Pair<String, String>> =
        flattenMap(config.toMap())

    private fun flattenMap(map: Map<String, Any?>, prefix: String = ""): Sequence<Pair<String, String>> = sequence {
        for ((key, rawValue) in map) {
            val path = if (prefix.isEmpty()) key else "$prefix.$key"
            when (rawValue) {
                null -> {}
                is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    yieldAll(flattenMap(rawValue as Map<String, Any?>, path))
                }
                is List<*> -> yieldAll(flattenList(rawValue, path))
                else -> yield(path to rawValue.toString())
            }
        }
    }

    private fun flattenList(list: List<*>, prefix: String): Sequence<Pair<String, String>> = sequence {
        list.forEachIndexed { index, element ->
            val path = "$prefix.$index"
            when (element) {
                null -> {}
                is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    yieldAll(flattenMap(element as Map<String, Any?>, path))
                }
                is List<*> -> yieldAll(flattenList(element, path))
                else -> yield(path to element.toString())
            }
        }
        yield("$prefix.size" to list.size.toString())
    }
}
