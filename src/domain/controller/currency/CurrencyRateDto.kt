package io.ducket.api.domain.controller.currency

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
    val source: String?,
    @JsonSerialize(using = LocalDateSerializer::class) val date: LocalDate,
) {
    constructor(currencyRate: CurrencyRate): this(
        baseCurrency = CurrencyDto(currencyRate.baseCurrency),
        quoteCurrency = CurrencyDto(currencyRate.quoteCurrency),
        rate = currencyRate.rate,
        source = currencyRate.dataSource,
        date = currencyRate.date,
    )
}
