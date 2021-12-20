package io.ducket.api.domain.controller.account

import domain.model.account.AccountType
import org.valiktor.functions.*

class AccountCreateDto(
    val name: String,
    val notes: String? = "",
    val currencyId: Long,
    val accountType: AccountType,
) {
    fun validate(): AccountCreateDto {
        org.valiktor.validate(this) {
            validate(AccountCreateDto::name).isNotNull().hasSize(1, 45)
            validate(AccountCreateDto::notes).hasSize(0, 128)
            validate(AccountCreateDto::currencyId).isNotZero().isPositive()
            validate(AccountCreateDto::accountType).isNotNull().isIn(AccountType.values().toList())
        }
        return this
    }
}