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

/**
 * Koin이라는 도구를 사용해서 "의존성 주입(Dependency Injection)"을 설정하는 곳입니다.
 *
 * 의존성 주입이란?
 * 요리사가 칼이 필요할 때, 직접 칼을 만드는 게 아니라(new Knife()),
 * 누군가가 잘 만들어진 칼을 쥐어주는 것(get())과 같습니다.
 *
 * 이렇게 하면 나중에 칼을 가위로 바꾸거나(테스트용 가짜 객체 교체 등) 하기가 쉬워집니다.
 */
fun Application.configureDI(config: ApplicationConfig) {
    install(Koin) {
        slf4jLogger()
        modules(appModule(config))
    }
}

/**
 * 여기서 실제로 어떤 '부품'들을 미리 만들어둘지 정의합니다.
 *
 * single { ... } : 앱이 실행되는 동안 딱 하나만 만들어서 계속 돌려씁니다. (Singleton)
 * get() : 이미 만들어진 다른 부품이 필요하면 달라고 요청합니다.
 */
fun appModule(config: ApplicationConfig) = module {
    single { config }
    
    // Repositories
    single { com.psmo.repository.ProductRepository(get()) }

    // Services
    single { TestService(get()) }
    single { UserService(get()) }
    single { JwtService(get()) }
    single { RefreshTokenService(get(), get()) }
    single { TelegramAuthService(get(), get(), get(), get()) }
    single { TelegramBotService(get()) }
    single { ProductService(get()) }

    single { ImageService(get()) }
    single { CloudflareStreamService(get()) } // Placeholder for Video Upload
    

}
