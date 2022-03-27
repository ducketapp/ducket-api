package io.ducket.api.domain.controller.budget

import java.math.BigDecimal
import java.math.RoundingMode

data class BudgetProgressDto(
    var percentage: BigDecimal = BigDecimal.ZERO,
    var amount: BigDecimal = BigDecimal.ZERO,
) {
    init {
        percentage = percentage.abs().setScale(2, RoundingMode.HALF_UP)
        amount = amount.abs().setScale(2, RoundingMode.HALF_UP)
    }
}