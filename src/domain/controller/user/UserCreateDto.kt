package io.ducket.api.domain.controller.user

import io.ducket.api.app.DEFAULT_SCALE
import io.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal

data class UserCreateDto(
    val name: String,
    val phone: String?,
    val email: String,
    val startBalance: BigDecimal = BigDecimal.ZERO,
    val currencyIsoCode: String,
    val password: String,
) {
    fun validate(): UserCreateDto {
        org.valiktor.validate(this) {
            validate(UserCreateDto::name).isNotBlank().hasSize(2, 64)
            validate(UserCreateDto::phone).isNotEmpty().startsWith("+")
            validate(UserCreateDto::email).isNotBlank().isEmail()
            validate(UserCreateDto::startBalance).scaleBetween(0, DEFAULT_SCALE)
            validate(UserCreateDto::password).isNotBlank().hasSize(4, 16)
            validate(UserCreateDto::currencyIsoCode).isNotBlank().hasSize(3, 3)
        }
        return this
    }
}