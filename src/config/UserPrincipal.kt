package io.ducket.api.config

import domain.model.user.User
import io.ducket.api.domain.controller.user.UserDto
import io.ktor.auth.*
import java.util.*

data class UserPrincipal(val id: String, val email: String) : Principal {
    constructor(user: User) : this(user.id, user.email)
}