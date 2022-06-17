package io.ducket.api.plugins

import io.ducket.api.auth.authorization.RoleBasedAuthorization
import io.ducket.api.auth.UserPrincipal
import io.ktor.application.*

fun Application.installAuthorization() {
    install(RoleBasedAuthorization) {
        getRole { principal ->
            (principal as UserPrincipal).role
        }
    }
}