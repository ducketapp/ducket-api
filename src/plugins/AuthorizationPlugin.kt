package org.expenny.service.plugins

import org.expenny.service.auth.authorization.Authorization
import org.expenny.service.auth.authentication.UserPrincipal
import io.ktor.server.application.*

fun Application.installAuthorizationPlugin() {
    install(Authorization) {
        getRole = { principal ->
            (principal as UserPrincipal).role
        }
    }
}