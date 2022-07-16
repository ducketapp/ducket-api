package dev.ducket.api.routes

import dev.ducket.api.domain.controller.currency.CurrencyController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.currencies(currencyController: CurrencyController) {
    route("/currencies") {
        get { currencyController.getCurrencies(this.context) }

        authenticate {
            route("/rates") {
                get { currencyController.getCurrencyRate(this.context) }
            }
        }
    }
}