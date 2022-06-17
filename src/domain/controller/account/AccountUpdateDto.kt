package io.ducket.api.domain.controller.account

import io.ducket.api.app.AccountType
import io.ducket.api.utils.declaredMemberPropertiesNull
import io.ducket.api.plugins.InvalidDataException
import org.valiktor.functions.*

class AccountUpdateDto(
    val name: String?,
    val notes: String?,
    val type: AccountType?,
) {
    fun validate(): AccountUpdateDto {
        org.valiktor.validate(this) {
            validate(AccountUpdateDto::name).hasSize(1, 64)
            validate(AccountUpdateDto::notes).hasSize(1, 128)
            validate(AccountUpdateDto::type).isNotNull()

            if (this@AccountUpdateDto.declaredMemberPropertiesNull()) throw InvalidDataException()
        }
        return this
    }
}