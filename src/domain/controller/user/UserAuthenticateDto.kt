package io.ducket.api.domain.controller.user

import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotBlank

data class UserAuthenticateDto(
    val email: String,
    val password: String,
) {
    fun validate(): UserAuthenticateDto {
        org.valiktor.validate(this) {
            validate(UserAuthenticateDto::email).isNotBlank().isEmail()
            validate(UserAuthenticateDto::password).isNotBlank()
        }
        return this
    }
}