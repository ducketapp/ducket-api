package io.ducket.api.domain.controller.user

import org.valiktor.functions.*

data class UserCreateDto(
    val name: String,
    val phone: String?,
    val email: String,
    val currencyIsoCode: String,
    val password: String,
) {

    fun validate(): UserCreateDto {
        org.valiktor.validate(this) {
            validate(UserCreateDto::name).isNotBlank().hasSize(2, 45)
            validate(UserCreateDto::phone).isNotEmpty().startsWith("+")
            validate(UserCreateDto::email).isNotBlank().isEmail()
            validate(UserCreateDto::password).isNotBlank().hasSize(4, 16)
            validate(UserCreateDto::currencyIsoCode).isNotBlank().hasSize(3, 3)
        }
        return this
    }
}