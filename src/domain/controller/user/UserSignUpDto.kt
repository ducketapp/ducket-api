package io.budgery.api.domain.controller.user

import org.valiktor.functions.*

data class UserSignUpDto(val name: String, val email: String, val currencyId: Int, val password: String) {
    fun validate() : UserSignUpDto {
        org.valiktor.validate(this) {
            validate(UserSignUpDto::name).isNotBlank().hasSize(2, 45)
            validate(UserSignUpDto::email).isNotBlank().isEmail()
            validate(UserSignUpDto::currencyId).isNotZero().isPositive()
            validate(UserSignUpDto::password).isNotBlank().hasSize(8, 14)
        }
        return this
    }
}