package org.expenny.service.test_data

import org.expenny.service.domain.model.currency.Currency
import org.expenny.service.domain.controller.currency.dto.CurrencyDto

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
