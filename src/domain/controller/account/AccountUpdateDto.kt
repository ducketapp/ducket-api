package io.ducket.api.domain.controller.account

import domain.model.account.AccountType
import io.ducket.api.plugins.InvalidDataError
import org.valiktor.functions.*

class AccountUpdateDto(val name: String?, val notes: String?, val accountType: AccountType?) {
    fun validate() : AccountUpdateDto {
        org.valiktor.validate(this) {
            validate(AccountUpdateDto::name).hasSize(1, 45)
            validate(AccountUpdateDto::notes).hasSize(0, 128)
            validate(AccountUpdateDto::accountType).isNotNull().isIn(AccountType.values().toList())

            if (name == null && notes == null && accountType == null) {
                throw InvalidDataError("No fields specified for updating")
            }
        }
        return this
    }
}