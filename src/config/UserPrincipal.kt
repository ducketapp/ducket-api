package io.ducket.api.config

import io.ktor.auth.*

data class UserPrincipal(val id: Long, val email: String) : Principal