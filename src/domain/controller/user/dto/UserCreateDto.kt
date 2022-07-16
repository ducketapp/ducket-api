package dev.ducket.api.domain.controller.user.dto

import dev.ducket.api.app.DEFAULT_SCALE
import dev.ducket.api.utils.hasLength
import dev.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal

data class UserCreateDto(
    val name: String,
    val phone: String?,
    val email: String,
    val currency: String,
    val password: String,
    val startBalance: BigDecimal,
) {
    fun validate(): UserCreateDto {
        org.valiktor.validate(this) {
            validate(UserCreateDto::name).isNotBlank().hasSize(2, 64)
            validate(UserCreateDto::phone).isNotEmpty().startsWith("+")
            validate(UserCreateDto::email).isNotBlank().isEmail()
            validate(UserCreateDto::password).isNotBlank().hasSize(4, 16)
            validate(UserCreateDto::currency).isNotBlank().hasLength(3)
            validate(UserCreateDto::startBalance).scaleBetween(0, DEFAULT_SCALE)
        }
        return this
    }
}