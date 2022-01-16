package io.ducket.api


import com.typesafe.config.ConfigFactory
import io.ducket.api.app.AppModule
import io.ktor.config.*
import io.ktor.server.testing.*
import org.koin.core.module.Module

fun withTestServer(
    diModules: MutableList<Module> = mutableListOf(AppModule.module),
    testBlock: TestApplicationEngine.() -> Unit,
) {
    withApplication(
        environment = createTestEnvironment {
            config = HoconApplicationConfig(ConfigFactory.load("development.conf"))
        },
        test = testBlock
    )
}
