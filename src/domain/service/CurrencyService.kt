package io.ducket.api.domain.service

import io.ducket.api.app.DEFAULT_ROUNDING
import io.ducket.api.app.DEFAULT_RATE_SCALE
import io.ducket.api.app.database.Transactional
import io.ducket.api.domain.controller.currency.CurrencyRateCreateDto
import io.ducket.api.clients.rates.ReferenceDto
import io.ducket.api.domain.controller.currency.CurrencyDto
import io.ducket.api.domain.controller.currency.CurrencyRateDto
import io.ducket.api.domain.repository.CurrencyRateRepository
import io.ducket.api.domain.repository.CurrencyRepository
import io.ducket.api.getLogger
import io.ducket.api.plugins.NoDataFoundException
import java.math.BigDecimal.*
import java.time.LocalDate

class CurrencyService(
    private val currencyRateRepository: CurrencyRateRepository,
    private val currencyRepository: CurrencyRepository,
): Transactional {
    private val logger = getLogger()

    suspend fun getCurrencyRate(baseCurrency: String, quoteCurrency: String, date: LocalDate): CurrencyRateDto {
        return if (date.isEqual(LocalDate.now()) || date.isBefore(LocalDate.now())) {
            currencyRateRepository.findLatest(baseCurrency, quoteCurrency)?.let { CurrencyRateDto(it) }
        } else {
            currencyRateRepository.findOneByDate(baseCurrency, quoteCurrency, date)?.let { CurrencyRateDto(it) }
        } ?: throw NoDataFoundException("Cannot find rate for ${baseCurrency}/${quoteCurrency} at $date")
    }

    suspend fun putCurrencyRates(references: List<ReferenceDto>, dataSource: String? = null) {
        val completeExchangeRates = references.flatMap { reference ->
            reference.rates
                .asSequence()
                .filter { it.value.toBigDecimalOrNull() != null && LocalDate.parse(it.date) != null }
                .map { rate ->
                    CurrencyRateCreateDto(
                        baseCurrency = reference.baseCurrency,
                        quoteCurrency = reference.currency,
                        rate = rate.value.toBigDecimal(),
                        date = LocalDate.parse(rate.date),
                        dataSource = dataSource,
                    )
                }
        }.createAllCombinations()

        logger.info("Inserting static exchange rates data: ${completeExchangeRates.size} item(s)")

        blockingTransaction {
            completeExchangeRates.chunked(250).forEach {
                logger.info("Inserting data chunk: ${it.size} item(s)")
                currencyRateRepository.insertBatch(it)
            }
        }
    }

    suspend fun getCurrencies(): List<CurrencyDto> {
        return currencyRepository.findAll().map { CurrencyDto(it) }
    }

    suspend fun deleteAllCurrencyRates() {
        currencyRateRepository.deleteAll()
    }

    private fun List<CurrencyRateCreateDto>.createAllCombinations(): List<CurrencyRateCreateDto> {
        return groupBy(CurrencyRateCreateDto::date).flatMap { dateToRates ->
            val rates = dateToRates.value
            val set = mutableSetOf<CurrencyRateCreateDto>()

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
