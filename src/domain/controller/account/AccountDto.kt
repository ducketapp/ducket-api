package io.budgery.api.domain.controller.account

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.budgery.api.InstantSerializer
import domain.model.account.Account
import java.math.BigDecimal
import java.time.Instant

@JsonPropertyOrder(value = ["id"])
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountDto(
    @JsonIgnore val account: Account,
    var balance: BigDecimal? = null,
    var numOfRecords: Int? = null
) {
    val id: Int = account.id
    val name: String = account.name
    val notes: String? = account.notes
    val accountType: AccountTypeDto = AccountTypeDto(account.type)
    val accountCurrency: CurrencyDto = CurrencyDto(account.currency)
}
