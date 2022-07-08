package io.ducket.api.domain.controller.account.dto

import com.fasterxml.jackson.annotation.JsonInclude
import domain.model.account.Account
import io.ducket.api.app.AccountType
import io.ducket.api.domain.controller.currency.dto.CurrencyDto
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountDto(
    val id: Long,
    val name: String,
    val balance: BigDecimal,
    val type: AccountType,
    val notes: String?,
    val currency: CurrencyDto,
) {
    constructor(account: Account) : this(
        id = account.id,
        name = account.name,
        balance = account.balance,
        type = account.type,
        notes = account.notes,
        currency = CurrencyDto(account.currency),
    )
}
