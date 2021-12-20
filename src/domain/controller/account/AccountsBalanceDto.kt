package io.ducket.api.domain.controller.account

import com.fasterxml.jackson.annotation.JsonInclude
import io.ducket.api.domain.controller.currency.CurrencyDto

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountsBalanceDto(
    val totalBalance: String,
    val currency: CurrencyDto,
    val appliedExchangeRates: List<AccountBalanceExchangeRateDto>,
    val accounts: List<AccountDto>,
)
