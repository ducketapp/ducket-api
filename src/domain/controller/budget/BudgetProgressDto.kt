package io.budgery.api.domain.controller.budget

import java.math.BigDecimal
import java.math.RoundingMode

class BudgetProgressDto(
    val recordsCount: Int = 0,
    val spendingCap: BigDecimal = BigDecimal.ZERO,
    progress: BigDecimal = BigDecimal.ZERO,
    spent: BigDecimal = BigDecimal.ZERO,
) {
    val progress: BigDecimal = progress.abs().setScale(1, RoundingMode.HALF_UP)
    val spent: BigDecimal = spent.abs().setScale(2, RoundingMode.HALF_UP)
}