package io.ducket.api.domain.repository

import io.ducket.api.app.database.Transactional
import io.ducket.api.domain.controller.currency.CurrencyRateCreateDto
import io.ducket.api.domain.model.currency.CurrencyRate
import io.ducket.api.domain.model.currency.CurrencyRateEntity
import io.ducket.api.domain.model.currency.CurrencyRatesTable
import org.jetbrains.exposed.sql.*
import java.time.LocalDate

class CurrencyRateRepository: Transactional {

    suspend fun insert(data: CurrencyRateCreateDto) = transactional {
        CurrencyRatesTable.insertIgnore {
            it[this.baseCurrencyIsoCode] = data.baseCurrency
            it[this.quoteCurrencyIsoCode] = data.quoteCurrency
            it[this.rate] = data.rate
            it[this.date] = data.date
            it[this.dataSource] = data.dataSource
        }
    }

    suspend fun insertBatch(data: List<CurrencyRateCreateDto>) = transactional {
        CurrencyRatesTable.batchInsert(data = data, ignore = true) { item ->
            this[CurrencyRatesTable.baseCurrencyIsoCode] = item.baseCurrency
            this[CurrencyRatesTable.quoteCurrencyIsoCode] = item.quoteCurrency
            this[CurrencyRatesTable.rate] = item.rate
            this[CurrencyRatesTable.date] = item.date
            this[CurrencyRatesTable.dataSource] = item.dataSource
        }
    }

    suspend fun findLatest(baseCurrency: String, quoteCurrency: String): CurrencyRate? = transactional {
        CurrencyRateEntity.find {
            CurrencyRatesTable.baseCurrencyIsoCode.eq(baseCurrency)
                .and(CurrencyRatesTable.quoteCurrencyIsoCode.eq(quoteCurrency))
        }.orderBy(CurrencyRatesTable.date to SortOrder.DESC).firstOrNull()?.toModel()
    }

    suspend fun findOneByDate(baseCurrency: String, quoteCurrency: String, date: LocalDate): CurrencyRate? = transactional {
        CurrencyRateEntity.find {
            CurrencyRatesTable.date.eq(date)
                .and(CurrencyRatesTable.baseCurrencyIsoCode.eq(baseCurrency))
                .and(CurrencyRatesTable.quoteCurrencyIsoCode.eq(quoteCurrency))
        }.firstOrNull()?.toModel()
    }

    suspend fun deleteAll() = transactional {
        CurrencyRatesTable.deleteAll()
    }
}