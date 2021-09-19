package io.budgery.api.domain.repository

import domain.model.currency.CurrenciesTable
import domain.model.currency.Currency
import domain.model.currency.CurrencyEntity
import org.jetbrains.exposed.sql.transactions.transaction

class CurrencyRepository {

    fun findOne(currencyIsoCode: String): Currency? = transaction {
        CurrencyEntity.find { CurrenciesTable.isoCode.eq(currencyIsoCode) }.firstOrNull()?.toModel()
    }
}