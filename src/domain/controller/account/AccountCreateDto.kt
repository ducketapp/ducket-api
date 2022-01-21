package io.ducket.api.domain.controller.account

import io.ducket.api.app.AccountType
import org.valiktor.functions.*

data class AccountCreateDto(
    val name: String,
    val notes: String? = "",
    val currencyIsoCode: String,
    val accountType: AccountType,
) {
    fun validate(): AccountCreateDto {
        org.valiktor.validate(this) {
            validate(AccountCreateDto::name).isNotNull().hasSize(1, 45)
            validate(AccountCreateDto::notes).hasSize(0, 128)
            validate(AccountCreateDto::currencyIsoCode).isNotBlank().hasSize(3, 3)
            validate(AccountCreateDto::accountType).isNotNull().isIn(AccountType.values().toList())
        }
        return this
    }
}