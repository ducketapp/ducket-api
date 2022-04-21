package io.ducket.api.domain.controller.user

import io.ducket.api.utils.declaredMemberPropertiesNull
import io.ducket.api.plugins.InvalidDataException
import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotEmpty
import org.valiktor.functions.startsWith

data class UserUpdateDto(
    val name: String? = null,
    val phone: String? = null,
    val password: String? = null,
) {

    fun validate() : UserUpdateDto {
        org.valiktor.validate(this) {
            validate(UserUpdateDto::name).isNotEmpty().hasSize(2, 64)
            validate(UserUpdateDto::phone).isNotEmpty().startsWith("+")
            validate(UserUpdateDto::password).isNotEmpty().hasSize(4, 16)

            if (this@UserUpdateDto.declaredMemberPropertiesNull()) throw InvalidDataException()
        }
        return this
    }
}