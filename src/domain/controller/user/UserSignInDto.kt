package io.budgery.api.domain.controller.user

import org.valiktor.functions.isNotBlank

data class UserSignInDto(val email: String, val password: String) {
    fun validate() : UserSignInDto {
        org.valiktor.validate(this) {
            validate(UserSignInDto::email).isNotBlank()
            validate(UserSignInDto::password).isNotBlank()
        }
        return this
    }
}