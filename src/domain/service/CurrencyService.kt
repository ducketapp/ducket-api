package org.expenny.service.domain.service

import org.expenny.service.app.DEFAULT_ROUNDING
import org.expenny.service.app.DEFAULT_RATE_SCALE
import org.expenny.service.app.database.Transactional
import org.expenny.service.domain.mapper.CurrencyMapper
import org.expenny.service.clients.rates.dto.ReferenceDto
import org.expenny.service.domain.controller.currency.dto.CurrencyDto
import org.expenny.service.domain.controller.currency.dto.CurrencyRateDto
import org.expenny.service.domain.model.currency.CurrencyRateCreate
import org.expenny.service.domain.repository.CurrencyRateRepository
import org.expenny.service.domain.repository.CurrencyRepository
import org.expenny.service.getLogger
import org.expenny.service.plugins.NoDataFoundException
import java.math.BigDecimal.*
import java.time.LocalDate

class CurrencyService(
    private val currencyRateRepository: CurrencyRateRepository,
    private val currencyRepository: CurrencyRepository,
): Transactional {

    suspend fun getCurrencyRate(baseCurrency: String, quoteCurrency: String, date: LocalDate): CurrencyRateDto {
        return if (date.isEqual(LocalDate.now()) || date.isAfter(LocalDate.now())) {
            currencyRateRepository.findLatest(baseCurrency, quoteCurrency)
        } else {
            currencyRateRepository.findOneByDate(baseCurrency, quoteCurrency, date)
        }?.let {
            CurrencyMapper.mapModelToDto(it)
        } ?: throw NoDataFoundException("Cannot find rate for ${baseCurrency}/${quoteCurrency} at $date")
    }

    suspend fun putCurrencyRates(references: List<ReferenceDto>, dataSource: String? = null) {
        val completeExchangeRates = references.flatMap { reference ->
            reference.rates
                .asSequence()
                .filter { it.value.toBigDecimalOrNull() != null && LocalDate.parse(it.date) != null }
                .map { rate ->
                    CurrencyRateCreate(
                        baseCurrency = reference.baseCurrency,
                        quoteCurrency = reference.currency,
                        rate = rate.value.toBigDecimal(),
                        date = LocalDate.parse(rate.date),
                        dataSource = dataSource,
                    )
                }
        }.createAllCombinations()

        getLogger().info("Inserting static exchange rates data: ${completeExchangeRates.size} item(s)")

        blockingTransaction {
            completeExchangeRates.chunked(250).forEach {
                currencyRateRepository.createBatch(it)
            }
        }
    }

    suspend fun getCurrencies(): List<CurrencyDto> {
        return currencyRepository.findAll().map { CurrencyMapper.mapModelToDto(it) }
    }

    suspend fun deleteAllCurrencyRates() {
        currencyRateRepository.deleteAll()
    }

    private fun List<CurrencyRateCreate>.createAllCombinations(): List<CurrencyRateCreate> {
        return groupBy(CurrencyRateCreate::date).flatMap { dateToRates ->
            val rates = dateToRates.value
            val set = mutableSetOf<CurrencyRateCreate>()

            for (r1 in rates) {
                // add initial rate
                set.add(r1.copy(rate = r1.rate.setScale(DEFAULT_RATE_SCALE, DEFAULT_ROUNDING)))

                for (r2 in rates) {
                    if (r1 == r2) {
                        // add reversed rate
                        set.add(r1.copy(
                            baseCurrency = r1.quoteCurrency,
                            quoteCurrency = r1.baseCurrency,
                            rate = ONE.divide(r1.rate, DEFAULT_RATE_SCALE, DEFAULT_ROUNDING),
                        ))
                    } else {
                        if (r1.baseCurrency == r2.baseCurrency) {
                            // add new combination created from 2 quote currencies of the same base currency
                            set.add(r1.copy(
                                baseCurrency = r1.quoteCurrency,
                                quoteCurrency = r2.quoteCurrency,
                                rate = r2.rate.divide(r1.rate, DEFAULT_RATE_SCALE, DEFAULT_ROUNDING),
                            ))
                        }
                    }
                }
            }

            return@flatMap set.toList()
        }
    }
}
