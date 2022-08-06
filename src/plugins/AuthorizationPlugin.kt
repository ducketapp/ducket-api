package dev.ducketapp.service.plugins

import dev.ducketapp.service.auth.authorization.Authorization
import dev.ducketapp.service.auth.authentication.UserPrincipal
import io.ktor.server.application.*

fun Application.installAuthorizationPlugin() {
    install(Authorization) {
        getRole = { principal ->
            (principal as UserPrincipal).role
        }
    }
}