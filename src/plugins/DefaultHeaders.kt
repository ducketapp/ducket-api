package io.ducket.api.plugins

import io.ktor.application.*
import io.ktor.features.*

fun Application.installDefaultHeaders() {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }
}