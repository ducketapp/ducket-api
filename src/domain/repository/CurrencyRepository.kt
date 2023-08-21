package org.expenny.service.domain.repository

import org.expenny.service.domain.model.currency.CurrenciesTable
import org.expenny.service.domain.model.currency.Currency
import org.expenny.service.domain.model.currency.CurrencyEntity
import org.expenny.service.app.database.Transactional

class CurrencyRepository: Transactional {

    suspend fun findOne(currency: String): Currency? = blockingTransaction {
        CurrencyEntity.find { CurrenciesTable.isoCode.eq(currency) }.firstOrNull()?.toModel()
    }

    suspend fun findAll(): List<Currency> = blockingTransaction {
        CurrencyEntity.all().map { it.toModel() }
    }
}