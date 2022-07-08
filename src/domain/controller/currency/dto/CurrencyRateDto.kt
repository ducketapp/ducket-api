package io.ducket.api.domain.controller.currency.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.domain.model.currency.CurrencyRate
import io.ducket.api.utils.LocalDateSerializer
import java.math.BigDecimal
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CurrencyRateDto(
    val baseCurrency: CurrencyDto,
    val quoteCurrency: CurrencyDto,
    val rate: BigDecimal,
    val dataSource: String?,
    @JsonSerialize(using = LocalDateSerializer::class) val date: LocalDate,
) {
    constructor(currencyRate: CurrencyRate): this(
        baseCurrency = CurrencyDto(currencyRate.baseCurrency),
        quoteCurrency = CurrencyDto(currencyRate.quoteCurrency),
        rate = currencyRate.rate,
        dataSource = currencyRate.dataSource,
        date = currencyRate.date,
    )
}
