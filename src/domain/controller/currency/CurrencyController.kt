package dev.ducket.api.domain.controller.currency

import dev.ducket.api.domain.service.CurrencyService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.util.*
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