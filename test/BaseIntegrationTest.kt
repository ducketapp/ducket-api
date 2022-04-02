package io.ducket.api

import com.typesafe.config.ConfigFactory
import io.ducket.api.app.di.AppModule
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.server.testing.*
import org.koin.core.module.Module
import org.koin.ktor.ext.Koin
import org.koin.logger.SLF4JLogger

abstract class BaseIntegrationTest {

    protected var testModule: Application.() -> Unit = { }
    protected var koinModules: Module? = null

    fun <R> withTestServer(testBlock: TestApplicationEngine.() -> Unit) {
        //    withApplication(
        //        environment = createTestEnvironment {
        //            config = HoconApplicationConfig(ConfigFactory.load("application.test.conf"))
        //        },
        //        test = testBlock
        //    )
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
                // module(testing = true, diModules = diModules)
            },
            test = testBlock
        )
    }
}