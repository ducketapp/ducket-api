package dev.ducket.api.plugins

import dev.ducket.api.auth.authorization.Authorization
import dev.ducket.api.auth.authentication.UserPrincipal
import io.ktor.server.application.*

fun Application.installAuthorizationPlugin() {
    install(Authorization) {
        getRole = { principal ->
            (principal as UserPrincipal).role
        }
    }
}