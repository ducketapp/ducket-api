package io.ducket.api.domain.controller.currency

import io.ducket.api.domain.service.CurrencyService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

class CurrencyController(
    private val currencyService: CurrencyService,
) {

    suspend fun getCurrenciesRates(ctx: ApplicationCall) {
        val currenciesRates = currencyService.getCurrenciesRates()
        ctx.respond(HttpStatusCode.OK, currenciesRates)
    }

    suspend fun getCurrencies(ctx: ApplicationCall) {
        val exchangeRates = currencyService.getCurrencies()
        ctx.respond(HttpStatusCode.OK, exchangeRates)
    }
}