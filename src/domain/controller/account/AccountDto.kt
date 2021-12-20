package io.ducket.api.domain.controller.account

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import domain.model.account.Account
import io.ducket.api.domain.controller.currency.CurrencyDto
import io.ducket.api.domain.controller.user.UserDto
import java.math.BigDecimal

@JsonPropertyOrder(value = ["id"])
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountDto(
    @JsonIgnore val account: Account,
    var balance: BigDecimal = BigDecimal.ZERO,
) {
    val id: Long = account.id
    val name: String = account.name
    val owner: UserDto = UserDto(account.user)
    val accountType: String = account.type.name
    val numOfRecords: Int = account.numOfRecords
    val accountCurrency: CurrencyDto = CurrencyDto(account.currency)
    val notes: String? = account.notes
}
