package io.ducket.api.domain.controller.currency

import java.math.BigDecimal

data class CurrencyRateDto(
    val currency: CurrencyDto,
    val rate: BigDecimal,
)
