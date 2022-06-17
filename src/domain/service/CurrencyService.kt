package io.ducket.api.domain.service

import io.ducket.api.domain.controller.currency.CurrencyDto
import io.ducket.api.domain.repository.CurrencyRepository


class CurrencyService(
    private val currencyRepository: CurrencyRepository,
) {
//    private val ecbClient: EcbClient by inject(EcbClient::class.java)
//
//    fun getCurrenciesRates(): List<CurrencyRateDto> {
//        val currenciesRatesMap = currencyRateProvider.getRatesMap()
//
//        return getCurrencies().map {
//            val currencyIsoCode = it.isoCode
//
//            if (currenciesRatesMap.containsKey(currencyIsoCode)) {
//                CurrencyRateDto(currency = it, rate = currenciesRatesMap[currencyIsoCode]!!)
//            } else {
//                throw InvalidDataException("Cannot find a rate for '$currencyIsoCode' currency")
//            }
//        }
//    }

    fun getCurrencies(): List<CurrencyDto> {
        return currencyRepository.findAll().map { CurrencyDto(it) }
    }
}