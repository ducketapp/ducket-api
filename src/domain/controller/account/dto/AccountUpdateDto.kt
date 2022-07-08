package io.ducket.api.domain.controller.account.dto

import io.ducket.api.app.AccountType
import org.valiktor.functions.*

class AccountUpdateDto(
    val name: String,
    val notes: String?,
    val type: AccountType,
) {
    fun validate(): AccountUpdateDto {
        org.valiktor.validate(this) {
            validate(AccountUpdateDto::name).hasSize(1, 64)
            validate(AccountUpdateDto::notes).hasSize(1, 128)
        }
        return this
    }
}