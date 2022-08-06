package dev.ducketapp.service.plugins

import dev.ducketapp.service.auth.authentication.UserPrincipal
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import org.slf4j.event.Level

fun Application.installCallLoggingPlugin() {
    install(CallLogging) {
        level = Level.INFO

        filter { call -> call.request.path().startsWith("/") }
        format { call ->
            val status = call.response.status()
            val uri = call.request.uri
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            val authentication = (call.principal() as UserPrincipal?)?.let { "id=${it.id}, role=${it.role}" } ?: "no-auth"

            "($status) [$httpMethod], $userAgent - [$authentication] $uri"
        }
    }
}