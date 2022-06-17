package io.ducket.api.domain.repository

import domain.model.category.CategoriesTable
import domain.model.currency.CurrenciesTable
import domain.model.currency.CurrencyEntity
import io.ducket.api.clients.rates.CurrencyExchangeRateDto
import io.ducket.api.domain.model.currency.CurrencyRate
import io.ducket.api.domain.model.currency.CurrencyRateEntity
import io.ducket.api.domain.model.currency.CurrencyRatesTable
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert

import org.jetbrains.exposed.sql.transactions.transaction

class CurrencyRateRepository {

    fun create(data: CurrencyExchangeRateDto) = transaction {
        CurrencyRatesTable.insert {
            it[this.baseCurrencyIsoCode] = data.baseCurrency
            it[this.quoteCurrencyIsoCode] = data.quoteCurrency
            it[this.rate] = data.rate
            it[this.date] = data.date
            it[this.dataSource] = data.dataSource
        }
    }

    fun createBatch(data: List<CurrencyExchangeRateDto>): List<CurrencyRate> = transaction {
        CurrencyRatesTable.batchInsert(data = data, ignore = true) { dataItem ->
            this[CurrencyRatesTable.baseCurrencyIsoCode] = dataItem.baseCurrency
            this[CurrencyRatesTable.quoteCurrencyIsoCode] = dataItem.quoteCurrency
            this[CurrencyRatesTable.rate] = dataItem.rate
            this[CurrencyRatesTable.date] = dataItem.date
            this[CurrencyRatesTable.dataSource] = dataItem.dataSource
        }.map { CurrencyRateEntity.wrapRow(it).toModel() }
    }

    fun deleteAll() = transaction {
        CurrencyRatesTable.deleteAll()
    }
}