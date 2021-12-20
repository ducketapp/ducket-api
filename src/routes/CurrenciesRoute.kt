package io.ducket.api.routes

import io.ducket.api.domain.controller.currency.CurrencyController
import io.ktor.auth.*
import io.ktor.routing.*

fun Route.currencies(currencyController: CurrencyController) {
    authenticate {
        route("/currencies") {
            get { currencyController.getCurrencies(this.context) }

            route("/rates") {
                get { currencyController.getCurrenciesRates(this.context) }
            }
        }
    }
}