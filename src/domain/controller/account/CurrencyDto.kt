package io.budgery.api.domain.controller.account

import com.fasterxml.jackson.annotation.JsonUnwrapped
import domain.model.currency.Currency

class CurrencyDto(@JsonUnwrapped val currency: Currency)