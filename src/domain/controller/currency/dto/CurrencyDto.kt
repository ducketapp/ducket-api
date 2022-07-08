package io.ducket.api.domain.controller.currency.dto

import domain.model.currency.Currency

data class CurrencyDto(
    val id: Long,
    val name: String,
    val area: String,
    val symbol: String,
    val isoCode: String,
) {
    constructor(currency: Currency): this(
        id = currency.id,
        name = currency.name,
        area = currency.area,
        symbol = currency.symbol,
        isoCode = currency.isoCode,
    )
}