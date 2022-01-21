package io.ducket.api.domain.controller.user

import org.valiktor.functions.isNotBlank

data class UserAuthDto(
    val email: String,
    val password: String,
) {

    fun validate(): UserAuthDto {
        org.valiktor.validate(this) {
            validate(UserAuthDto::email).isNotBlank()
            validate(UserAuthDto::password).isNotBlank()
        }
        return this
    }
}