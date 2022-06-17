package io.ducket.api.clients.rates

import java.math.BigDecimal
import java.time.LocalDate

data class CurrencyExchangeRateDto(
    val baseCurrency: String,
    val quoteCurrency: String,
    val rate: BigDecimal,
    val date: LocalDate,
    val dataSource: String?,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CurrencyExchangeRateDto

        if (baseCurrency != other.baseCurrency) return false
        if (quoteCurrency != other.quoteCurrency) return false
        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = baseCurrency.hashCode()
        result = 31 * result + quoteCurrency.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}
