package io.ducket.api.domain.repository

import domain.model.currency.CurrenciesTable
import domain.model.currency.Currency
import domain.model.currency.CurrencyEntity
import io.ducket.api.app.database.Transactional

class CurrencyRepository: Transactional {

    suspend fun findOne(currency: String): Currency? = transactional {
        CurrencyEntity.find { CurrenciesTable.isoCode.eq(currency) }.firstOrNull()?.toModel()
    }

    suspend fun findAll(): List<Currency> = transactional {
        CurrencyEntity.all().map { it.toModel() }
    }
}