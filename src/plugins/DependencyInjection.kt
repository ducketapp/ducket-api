package io.ducket.api.plugins

import io.ducket.api.app.di.AppModule
import io.ktor.application.*
import org.koin.ktor.ext.Koin

fun Application.installDependencyInjection() {
    install(Koin) {
        // SLF4JLogger()
        modules(
            AppModule.configurationModule,
            AppModule.authenticationModule,
            AppModule.databaseModule,
            AppModule.schedulerModule,
            AppModule.repositoryModule,
            AppModule.serviceModule,
            AppModule.controllerModule,
            AppModule.clientModule,
        )
    }
}