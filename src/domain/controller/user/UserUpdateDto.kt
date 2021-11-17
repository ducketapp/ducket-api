package io.ducket.api.domain.controller.user

import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotEmpty
import org.valiktor.functions.startsWith
import java.lang.IllegalArgumentException

data class UserUpdateDto(val name: String?, val phone: String?, val password: String?) {
    fun validate() : UserUpdateDto {
        org.valiktor.validate(this) {
            validate(UserUpdateDto::name).isNotEmpty().hasSize(2, 45)
            validate(UserUpdateDto::phone).isNotEmpty().startsWith("+").hasSize(11)
            validate(UserUpdateDto::password).isNotEmpty().hasSize(8, 14)

            if (name == null && phone == null && password == null) {
                throw IllegalArgumentException("No one field is specified for updating")
            }
        }
        return this
    }
}