package io.ducket.api.domain.controller.currency

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CurrencyRateDto(
    val currency: CurrencyDto,
    val rate: BigDecimal,
)
