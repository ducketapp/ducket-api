package io.ducket.api.domain.controller.budget

import com.fasterxml.jackson.annotation.JsonInclude
import io.ducket.api.domain.model.budget.BudgetPeriodLimit
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BudgetPeriodLimitDto(
    val id: Long,
    val default: Boolean,
    val limit: BigDecimal,
    val period: String,
) {
    constructor(budgetPeriodLimit: BudgetPeriodLimit): this(
        id = budgetPeriodLimit.id,
        default = budgetPeriodLimit.default,
        limit = budgetPeriodLimit.limit,
        period = budgetPeriodLimit.period,
    )
}
