package io.ducket.api.domain.controller.currency

import com.fasterxml.jackson.annotation.JsonIgnore
import domain.model.currency.Currency

data class CurrencyDto(@JsonIgnore val currency: Currency) {
    val id: Long = currency.id
    val name: String = currency.name
    val area: String = currency.area
    val symbol: String = currency.symbol
    val isoCode: String = currency.isoCode
}