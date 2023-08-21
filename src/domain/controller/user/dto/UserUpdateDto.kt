package org.expenny.service.domain.controller.user.dto

import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotBlank
import org.valiktor.functions.isNotEmpty
import org.valiktor.functions.startsWith

data class UserUpdateDto(
    val name: String,
    val password: String,
) {
    fun validate(): UserUpdateDto {
        org.valiktor.validate(this) {
            validate(UserUpdateDto::name).isNotBlank().hasSize(2, 64)
            validate(UserUpdateDto::password).isNotBlank().hasSize(4, 16)
        }
        return this
    }
}