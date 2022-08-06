package dev.ducketapp.service

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.koin.core.module.Module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger

abstract class BaseIntegrationTest {

    protected var testModule: Application.() -> Unit = { }
    protected var koinModules: Module? = null

    fun <R> withTestServer(testBlock: TestApplicationEngine.() -> Unit) {
        withTestApplication(
            moduleFunction = {
                (environment.config as HoconApplicationConfig).apply {
                    HoconApplicationConfig(ConfigFactory.load("application.test.conf"))
                }
                install(Koin) {
                    SLF4JLogger()
                    koinModules?.let { modules(it) }
                }
                testModule()
                // module(testing = true)
            },
            test = testBlock
        )
    }
}