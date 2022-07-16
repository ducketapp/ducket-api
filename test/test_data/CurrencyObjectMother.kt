package dev.ducket.api.test_data

import dev.ducket.api.domain.model.currency.Currency
import dev.ducket.api.domain.controller.currency.dto.CurrencyDto

class CurrencyObjectMother {
    companion object {
        fun currency(): Currency = Currency(
            id = 1,
            area = "United States",
            name = "United States dollar",
            symbol = "$",
            isoCode = "USD",
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
