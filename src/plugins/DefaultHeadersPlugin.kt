package org.expenny.service.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*

fun Application.installDefaultHeadersPlugin() {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }
}