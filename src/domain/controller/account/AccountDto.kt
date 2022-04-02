package io.ducket.api.domain.controller.account

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import domain.model.account.Account
import io.ducket.api.domain.controller.currency.CurrencyDto
import io.ducket.api.domain.controller.user.UserDto
import java.math.BigDecimal

@JsonPropertyOrder(value = ["id"])
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountDto(
    val id: Long,
    val name: String,
    var balance: BigDecimal,
    val owner: UserDto,
    val accountType: String,
    val recordsCount: Int,
    val accountCurrency: CurrencyDto,
    val notes: String?,
) {
    constructor(account: Account, balance: BigDecimal = BigDecimal.ZERO) : this(
        id = account.id,
        name = account.name,
        balance = balance,
        owner = UserDto(account.user),
        accountType = account.type.name,
        recordsCount = account.recordsCount,
        accountCurrency = CurrencyDto(account.currency),
        notes = account.notes,
    )
}
