package io.ducket.api.domain.controller.user

import org.valiktor.functions.*

data class UserSignUpDto(
    val name: String,
    val phone: String?,
    val email: String,
    val currencyId: String,
    val password: String,
) {
    fun validate(): UserSignUpDto {
        org.valiktor.validate(this) {
            validate(UserSignUpDto::name).isNotBlank().hasSize(2, 45)
            validate(UserSignUpDto::phone).isNotEmpty().startsWith("+").hasSize(11)
            validate(UserSignUpDto::email).isNotBlank().isEmail()
            validate(UserSignUpDto::currencyId).isNotBlank()
            validate(UserSignUpDto::password).isNotBlank().hasSize(8, 14)
        }
        return this
    }
}