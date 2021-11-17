package io.ducket.api.domain.controller.account

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.InstantSerializer
import domain.model.account.Account
import java.math.BigDecimal
import java.time.Instant

@JsonPropertyOrder(value = ["id"])
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountDto(
    @JsonIgnore val account: Account,
    var balance: BigDecimal? = BigDecimal.ZERO,
) {
    val id: String = account.id
    val name: String = account.name
    val notes: String? = account.notes
    val accountType: String = account.type.name
    val numOfRecords: Int = account.numOfRecords
    val accountCurrency: CurrencyDto = CurrencyDto(account.currency)
}
