package io.budgery.api.domain.controller.budget

import java.math.BigDecimal

class BudgetProgressDto(
    val percentage: Double,
    val totalTransactions: Int,
    val totalAmount: BigDecimal,
)