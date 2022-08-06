package dev.ducketapp.service.domain.repository

import dev.ducketapp.service.domain.model.currency.CurrenciesTable
import dev.ducketapp.service.domain.model.currency.Currency
import dev.ducketapp.service.domain.model.currency.CurrencyEntity
import dev.ducketapp.service.app.database.Transactional

class CurrencyRepository: Transactional {

    suspend fun findOne(currency: String): Currency? = blockingTransaction {
        CurrencyEntity.find { CurrenciesTable.isoCode.eq(currency) }.firstOrNull()?.toModel()
    }

    suspend fun findAll(): List<Currency> = blockingTransaction {
        CurrencyEntity.all().map { it.toModel() }
    }
}