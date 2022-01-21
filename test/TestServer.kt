package io.ducket.api


import com.typesafe.config.ConfigFactory
import io.ducket.api.app.di.AppModule
import io.ktor.config.*
import io.ktor.server.testing.*
import org.koin.core.module.Module

fun withTestServer(
    diModules: MutableList<Module> = mutableListOf(AppModule.module),
    testBlock: TestApplicationEngine.() -> Unit,
) {
    withApplication(
        environment = createTestEnvironment {
            config = HoconApplicationConfig(ConfigFactory.load("application.conf"))
        },
        test = testBlock
    )
}
