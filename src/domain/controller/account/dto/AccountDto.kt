package dev.ducket.api.domain.controller.account.dto

import com.fasterxml.jackson.annotation.JsonInclude
import dev.ducket.api.domain.model.account.Account
import dev.ducket.api.app.AccountType
import dev.ducket.api.domain.controller.currency.dto.CurrencyDto
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountDto(
    val id: Long,
    val extId: String?,
    val name: String,
    val startBalance: BigDecimal,
    val totalBalance: BigDecimal,
    val type: AccountType,
    val notes: String?,
    val currency: CurrencyDto,
) {
    constructor(account: Account) : this(
        id = account.id,
        extId = account.extId,
        name = account.name,
        startBalance = account.startBalance,
        totalBalance = account.totalBalance,
        type = account.type,
        notes = account.notes,
        currency = CurrencyDto(account.currency),
    )
}
