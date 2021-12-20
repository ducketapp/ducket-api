package io.ducket.api.domain.controller.account

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal


@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountBalanceExchangeRateDto(
    val baseCurrencyIsoCode: String,
    val termCurrencyIsoCode: String,
    val rate: BigDecimal,
)
