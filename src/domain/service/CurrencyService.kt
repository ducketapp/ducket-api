package io.ducket.api.domain.service

import io.ducket.api.ExchangeRateClient
import io.ducket.api.domain.controller.currency.CurrencyDto
import io.ducket.api.domain.controller.currency.CurrencyRateDto
import io.ducket.api.domain.repository.CurrencyRepository
import io.ducket.api.plugins.InvalidDataError


class CurrencyService(
    private val currencyRepository: CurrencyRepository
) {

    fun getCurrenciesRates(): List<CurrencyRateDto> {
        val currenciesRatesMap = ExchangeRateClient.getRatesMap()

        return getCurrencies().map {
            val currencyIso = it.isoCode

            if (currenciesRatesMap.containsKey(currencyIso)) {
                CurrencyRateDto(currency = it, rate = currenciesRatesMap[currencyIso]!!)
            } else {
                throw InvalidDataError("Cannot find a rate for $currencyIso currency")
            }
        }
    }

    fun getCurrencies(): List<CurrencyDto> {
        return currencyRepository.findAll().map { CurrencyDto(it) }
    }
}