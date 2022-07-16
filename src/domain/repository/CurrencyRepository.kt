package dev.ducket.api.domain.repository

import dev.ducket.api.domain.model.currency.CurrenciesTable
import dev.ducket.api.domain.model.currency.Currency
import dev.ducket.api.domain.model.currency.CurrencyEntity
import dev.ducket.api.app.database.Transactional

class CurrencyRepository: Transactional {

    suspend fun findOne(currency: String): Currency? = blockingTransaction {
        CurrencyEntity.find { CurrenciesTable.isoCode.eq(currency) }.firstOrNull()?.toModel()
    }

    suspend fun findAll(): List<Currency> = blockingTransaction {
        CurrencyEntity.all().map { it.toModel() }
    }
}