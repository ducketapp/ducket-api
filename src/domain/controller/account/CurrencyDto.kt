package io.budgery.api.domain.controller.account

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonUnwrapped
import domain.model.currency.Currency

class CurrencyDto(@JsonIgnore val currency: Currency) {
    val id: Int = currency.id
    val territory: String = currency.territory
    val name: String = currency.name
    val symbol: String = currency.symbol
    val isoCode: String = currency.isoCode
}