package io.ducket.api.domain.controller.budget

import java.time.LocalDate

class BudgetPeriodBoundsDto(
    val type: String,
    bounds: Pair<LocalDate, LocalDate>,
) {
    val lowerBound: String = bounds.first.toString()
    val upperBound: String = bounds.second.toString()
}