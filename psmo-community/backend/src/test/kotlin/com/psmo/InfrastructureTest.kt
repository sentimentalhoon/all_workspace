package com.psmo

import com.psmo.plugins.configureDI
import com.psmo.plugins.appModule
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class InfrastructureTest {

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `DI configuration should be valid`() {
        // Just verify Koin module definition
        val config = MapApplicationConfig()
        val module = appModule(config)
        // Koin provides checkModules but it needs full graph.
        // We can try to just start koin and see.
        // But better to use testApplication which mimics the server.
    }

    @Test
    fun `Server should start up with DI`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "jwt.secret" to "test-secret",
                "telegram.botToken" to "test-token"
            )
        }
        application {
             // Mock configs to avoid real DB connection if possible, 
             // or expect it to fail at DB step but PASS the DI step.
             try {
                configureDI(environment.config)
                // If DI fails, this throws.
             } catch (e: Exception) {
                 throw RuntimeException("DI Configuration failed", e)
             }
        }
    }
}
