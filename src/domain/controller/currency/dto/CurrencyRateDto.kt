package dev.ducketapp.service.domain.controller.currency.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CurrencyRateDto(
    val baseCurrency: CurrencyDto,
    val quoteCurrency: CurrencyDto,
    val rate: BigDecimal,
    val dataSource: String?,
    val date: LocalDate,
)
