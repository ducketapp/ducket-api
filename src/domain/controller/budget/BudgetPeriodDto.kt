package io.budgery.api.domain.controller.budget

import java.time.LocalDate

class BudgetPeriodDto(
    val type: String,
    bounds: Pair<LocalDate, LocalDate>,
) {
    val lowerBound: String = bounds.first.toString()
    val upperBound: String = bounds.second.toString()
}