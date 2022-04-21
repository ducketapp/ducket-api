package io.ducket.api.domain.controller.user

import org.valiktor.functions.hasSize
import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotBlank

data class UserAuthDto(
    val email: String,
    val password: String,
) {
    fun validate(): UserAuthDto {
        org.valiktor.validate(this) {
            validate(UserAuthDto::email).isNotBlank().isEmail().hasSize(5, 64)
            validate(UserAuthDto::password).isNotBlank()
        }
        return this
    }
}