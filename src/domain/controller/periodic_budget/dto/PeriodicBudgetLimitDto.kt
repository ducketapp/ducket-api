package domain.controller.periodic_budget.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import domain.model.periodic_budget.PeriodicBudgetLimit
import io.ducket.api.utils.LocalDateSerializer
import java.math.BigDecimal
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PeriodicBudgetLimitDto(
    val id: Long,
    val default: Boolean,
    val limit: BigDecimal,
    @JsonSerialize(using = LocalDateSerializer::class) val fromDate: LocalDate,
    @JsonSerialize(using = LocalDateSerializer::class) val toDate: LocalDate,
) {
    constructor(budgetPeriodLimit: PeriodicBudgetLimit): this(
        id = budgetPeriodLimit.id,
        default = budgetPeriodLimit.default,
        limit = budgetPeriodLimit.limit,
        fromDate = budgetPeriodLimit.fromDate,
        toDate = budgetPeriodLimit.toDate,
    )
}
