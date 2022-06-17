package io.ducket.api.domain.service

import domain.model.currency.DEFAULT_ROUNDING
import domain.model.currency.DEFAULT_SCALE
import io.ducket.api.clients.rates.CurrencyExchangeRateDto
import io.ducket.api.clients.rates.ReferenceDto
import io.ducket.api.domain.repository.CurrencyRateRepository
import io.ducket.api.domain.repository.CurrencyRepository
import io.ducket.api.getLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal.*
import java.time.LocalDate

class CurrencyRateService(
    private val currencyRateRepository: CurrencyRateRepository,
    private val currencyRepository: CurrencyRepository,
) {

    fun putCurrencyRates(references: List<ReferenceDto>, dataSource: String? = null) {
        val exchangeRates = references.flatMap { reference ->
            reference.rates
                .asSequence()
                .filter { it.value.toBigDecimalOrNull() != null && LocalDate.parse(it.date) != null }
                .map { rate ->
                    CurrencyExchangeRateDto(
                        baseCurrency = reference.baseCurrency,
                        quoteCurrency = reference.currency,
                        rate = rate.value.toBigDecimal(),
                        date = LocalDate.parse(rate.date),
                        dataSource = dataSource,
                    )
                }
        }

        getLogger().debug("Creating exchange rates combinations from set: ${exchangeRates.size} item(s)")

        exchangeRates.createAllCombinations().also { data ->
            getLogger().debug("Inserting exchange rates data: ${data.size} item(s)")

            currencyRateRepository.deleteAll()
            data.forEach {
                currencyRateRepository.create(it)
            }
        }

        getLogger().debug("Exchange rates data insertion was completed")
    }

    private fun List<CurrencyExchangeRateDto>.createAllCombinations(): List<CurrencyExchangeRateDto> {
        return groupBy(CurrencyExchangeRateDto::date).flatMap { dateToRates ->
            val rates = dateToRates.value
            val set = mutableSetOf<CurrencyExchangeRateDto>()

            for (r1 in rates) {
                // add initial rate
                set.add(r1.copy(rate = r1.rate.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING)))

                for (r2 in rates) {
                    if (r1 == r2) {
                        // add reversed rate
                        set.add(r1.copy(
                            baseCurrency = r1.quoteCurrency,
                            quoteCurrency = r1.baseCurrency,
                            rate = ONE.divide(r1.rate, DEFAULT_SCALE, DEFAULT_ROUNDING),
                        ))
                    } else {
                        if (r1.baseCurrency == r2.baseCurrency) {
                            // add new combination created from 2 quote currencies of the same base currency
                            set.add(r1.copy(
                                baseCurrency = r1.quoteCurrency,
                                quoteCurrency = r2.quoteCurrency,
                                rate = r2.rate.divide(r1.rate, DEFAULT_SCALE, DEFAULT_ROUNDING),
                            ))
                        }
                    }
                }
            }

            return@flatMap set.toList()
        }
    }
}