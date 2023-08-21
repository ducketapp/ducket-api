package org.expenny.service.auth.authentication

import io.ktor.server.auth.*

data class UserPrincipal(
    val id: Long,
    val email: String,
    val role: UserRole,
): Principal