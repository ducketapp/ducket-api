package io.ducket.api.domain.controller.budget

import java.math.BigDecimal
import java.math.RoundingMode

class BudgetProgressDto(
    val transactionsCount: Int = 0,
    val spendingCap: BigDecimal = BigDecimal.ZERO,
    var progressPercentage: BigDecimal = BigDecimal.ZERO,
    var moneySpent: BigDecimal = BigDecimal.ZERO,
) {
    init {
        progressPercentage = progressPercentage.abs().setScale(1, RoundingMode.HALF_UP)
        moneySpent = moneySpent.abs().setScale(2, RoundingMode.HALF_UP)
    }
}