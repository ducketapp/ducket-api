package io.budgery.api.config

import domain.model.user.User
import io.budgery.api.domain.controller.user.UserDto
import io.ktor.auth.*
import java.util.*

data class UserPrincipal(val id: Int, val uuid: UUID, val email: String) : Principal {
    constructor(user: User) : this(user.id, user.uuid, user.email)
}