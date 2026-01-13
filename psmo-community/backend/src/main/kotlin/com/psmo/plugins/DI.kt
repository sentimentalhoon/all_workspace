package com.psmo.plugins

import com.psmo.service.*
import io.ktor.server.application.*
import io.ktor.server.config.ApplicationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

fun Application.configureDI(config: ApplicationConfig) {
    install(Koin) {
        slf4jLogger()
        modules(appModule(config))
    }
}

fun appModule(config: ApplicationConfig) = module {
    single { config }
    
    // Services
    single { TestService(get()) }
    single { UserService(get()) }
    single { JwtService(get()) }
    single { RefreshTokenService(get(), get()) }
    single { TelegramAuthService(get(), get(), get(), get()) }
    single { TelegramBotService(get()) }
    single { ChatService(get()) }
    single { CloudflareStreamService(get()) } // Placeholder for Video Upload
    
    // Coroutine Scopes for Chat
    single { 
        val subScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val broadcastScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        ChatRoomManager(get(), get(), subScope, broadcastScope)
    }
}
