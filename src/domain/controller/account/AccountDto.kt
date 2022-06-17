package io.ducket.api.domain.controller.account

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import domain.model.account.Account
import io.ducket.api.app.AccountType
import io.ducket.api.domain.controller.currency.CurrencyDto
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountDto(
    val id: Long,
    val name: String,
    val type: AccountType,
    val notes: String?,
    val totalBalance: BigDecimal,
    val currency: CurrencyDto,
) {
    constructor(account: Account) : this(
        id = account.id,
        name = account.name,
        type = account.type,
        notes = account.notes,
        totalBalance = account.balance,
        currency = CurrencyDto(account.currency),
    )
}
