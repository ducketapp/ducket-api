package io.ducket.api.domain.repository

import io.ducket.api.domain.model.currency.CurrenciesTable
import io.ducket.api.domain.model.currency.Currency
import io.ducket.api.domain.model.currency.CurrencyEntity
import io.ducket.api.app.database.Transactional

class CurrencyRepository: Transactional {

    suspend fun findOne(currency: String): Currency? = blockingTransaction {
        CurrencyEntity.find { CurrenciesTable.isoCode.eq(currency) }.firstOrNull()?.toModel()
    }

    suspend fun findAll(): List<Currency> = blockingTransaction {
        CurrencyEntity.all().map { it.toModel() }
    }
}