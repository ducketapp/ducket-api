package io.ducket.api.domain.service

import io.ducket.api.CurrencyRatesClient
import io.ducket.api.domain.controller.currency.CurrencyDto
import io.ducket.api.domain.controller.currency.CurrencyRateDto
import io.ducket.api.domain.repository.CurrencyRepository
import io.ducket.api.plugins.InvalidDataError
import org.koin.java.KoinJavaComponent.inject


class CurrencyService(
    private val currencyRepository: CurrencyRepository,
) {
    private val currencyRatesClient: CurrencyRatesClient by inject(CurrencyRatesClient::class.java)

    fun getCurrenciesRates(): List<CurrencyRateDto> {
        val currenciesRatesMap = currencyRatesClient.getRatesMap()

        return getCurrencies().map {
            val currencyIsoCode = it.isoCode

            if (currenciesRatesMap.containsKey(currencyIsoCode)) {
                CurrencyRateDto(currency = it, rate = currenciesRatesMap[currencyIsoCode]!!)
            } else {
                throw InvalidDataError("Cannot find a rate for $currencyIsoCode currency")
            }
        }
    }

    fun getCurrencies(): List<CurrencyDto> {
        return currencyRepository.findAll().map { CurrencyDto(it) }
    }
}