package dev.ducket.api.domain.controller.user.dto

import dev.ducket.api.utils.hasLength
import org.valiktor.functions.*

data class UserCreateDto(
    val name: String,
    val email: String,
    val currency: String,
    val password: String,
) {
    fun validate(): UserCreateDto {
        org.valiktor.validate(this) {
            validate(UserCreateDto::name).isNotBlank().hasSize(2, 64)
            validate(UserCreateDto::email).isNotBlank().isEmail()
            validate(UserCreateDto::password).isNotBlank().hasSize(4, 16)
            validate(UserCreateDto::currency).isNotBlank().hasLength(3)
        }
        return this
    }
}