package io.ducket.api.test_data

import domain.model.currency.Currency

class CurrencyObjectMother {
    companion object {
        fun default(): Currency = Currency(
            id = 1,
            area = "",
            name = "",
            symbol = "",
            isoCode = "",
        )

        fun eur(): Currency = Currency(
            id = 2,
            area = "EU",
            name = "Euro",
            symbol = "€",
            isoCode = "EUR",
        )

        fun usd(): Currency = Currency(
            id = 3,
            area = "United States",
            name = "United States dollar",
            symbol = "$",
            isoCode = "USD",
        )
    }
}
