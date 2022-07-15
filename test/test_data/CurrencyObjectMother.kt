package io.ducket.api.test_data

import io.ducket.api.domain.model.currency.Currency
import io.ducket.api.domain.controller.currency.dto.CurrencyDto

class CurrencyObjectMother {
    companion object {
        fun currency(): Currency = Currency(
            id = 1,
            area = "United States",
            name = "United States dollar",
            symbol = "$",
            isoCode = "USD",
        )

        fun eur(): Currency = Currency(
            id = 2,
            area = "EU",
            name = "Euro",
            symbol = "€",
            isoCode = "EUR",
        )

        fun currencyDto(): CurrencyDto = CurrencyDto(
            id = 1,
            area = "United States",
            name = "United States dollar",
            symbol = "$",
            isoCode = "USD",
        )
    }
}
