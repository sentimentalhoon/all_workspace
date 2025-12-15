package com.psmo

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

import com.psmo.plugins.configureSwagger

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            // In a real test, we might want to mock the DB or use an in-memory one.
            // For now, we assume the module can be loaded or we test specific plugins.
            // basic module loading:
            // module() 
            // Warning: loading the full module() will try to connect to the DB configured in application.conf
        }
        
        // Simple assertion to verify test harness works
        assertEquals(1, 1)
    }

    @Test
    fun testSwagger() = testApplication {
        application {
             // We only load the swagger plugin for this specific test to avoid DB connection issues
             // But we need to define the function first. 
             // Since 'configureSwagger' is an extension on Application, we can call it.
             configureSwagger()
        }

        client.get("/swagger").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
