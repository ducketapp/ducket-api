package io.ducket.api.plugins

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import org.slf4j.event.Level

fun Application.installCallLogging() {
    install(CallLogging) {
        level = Level.DEBUG

        filter { call -> call.request.path().startsWith("/") }
        format { call ->
            val status = call.response.status()
            val uri = call.request.uri
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            "($status) [$httpMethod], $userAgent - $uri"
        }
    }
}