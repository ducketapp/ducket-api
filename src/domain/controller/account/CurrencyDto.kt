package io.ducket.api.domain.controller.account

import com.fasterxml.jackson.annotation.JsonIgnore
import domain.model.currency.Currency

data class CurrencyDto(@JsonIgnore val currency: Currency) {
    val id: String = currency.id
    val name: String = currency.name
    val area: String = currency.area
    val symbol: String = currency.symbol
    val isoCode: String = currency.isoCode
}