package io.ducket.api.plugins

import io.ducket.api.auth.UserPrincipal
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.request.*
import org.slf4j.MDC
import org.slf4j.event.Level

fun Application.installCallLogging() {
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