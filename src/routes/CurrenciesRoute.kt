package org.expenny.service.routes

import org.expenny.service.domain.controller.currency.CurrencyController
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