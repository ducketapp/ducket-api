package io.ducket.api.domain.controller.user

import io.ducket.api.auth.UserRole
import org.valiktor.functions.hasSize
import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotBlank
import org.valiktor.functions.isNotEqualTo

data class UserAuthorizeDto(
    val email: String,
    val role: UserRole,
) {
    fun validate(): UserAuthorizeDto {
        org.valiktor.validate(this) {
            validate(UserAuthorizeDto::email).isNotBlank().isEmail().hasSize(5, 64)
            validate(UserAuthorizeDto::role).isNotEqualTo(UserRole.SUPER_USER)
        }
        return this
    }
}
