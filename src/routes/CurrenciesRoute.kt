package io.ducket.api.routes

import io.ducket.api.domain.controller.currency.CurrencyController
import io.ktor.routing.*

fun Route.currencies(currencyController: CurrencyController) {
    route("/currencies") {
        get { currencyController.getCurrencies(this.context) }

        route("/app/rates_data") {
            get { currencyController.getCurrenciesRates(this.context) }
        }
    }
}