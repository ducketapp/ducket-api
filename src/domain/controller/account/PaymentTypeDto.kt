package io.budgery.api.domain.controller.account

import com.fasterxml.jackson.annotation.JsonUnwrapped
import domain.model.transaction.PaymentType

class PaymentTypeDto(@JsonUnwrapped val paymentType: PaymentType)