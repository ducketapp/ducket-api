package io.ducket.api.domain.controller.currency

import io.ducket.api.domain.service.CurrencyService
import io.ducket.api.plugins.InvalidDataException
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.*
import java.time.LocalDate

class CurrencyController(
    private val currencyService: CurrencyService,
) {

    suspend fun getCurrencyRate(ctx: ApplicationCall) {
        val baseCurrency = ctx.request.queryParameters.getOrFail<String>("base")
        val quoteCurrency = ctx.request.queryParameters.getOrFail<String>("quote")
        val date = ctx.request.queryParameters["date"]?.let { LocalDate.parse(it) } ?: LocalDate.now()

        val currencyRate = currencyService.getCurrencyRate(baseCurrency, quoteCurrency, date)
        ctx.respond(HttpStatusCode.OK, currencyRate)
    }

    suspend fun getCurrencies(ctx: ApplicationCall) {
        val currencies = currencyService.getCurrencies()
        ctx.respond(HttpStatusCode.OK, currencies)
    }
}