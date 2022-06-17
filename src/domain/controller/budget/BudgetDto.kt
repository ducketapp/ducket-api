package io.ducket.api.domain.controller.budget

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.app.BudgetPeriodType
import io.ducket.api.utils.LocalDateSerializer
import io.ducket.api.domain.controller.account.AccountDto
import io.ducket.api.domain.controller.category.TypedCategoryDto
import io.ducket.api.domain.controller.currency.CurrencyDto
import io.ducket.api.domain.model.budget.Budget
import io.ducket.api.domain.model.budget.BudgetPeriodLimit
import java.math.BigDecimal
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BudgetDto(
    val id: Long,
    val title: String,
    val notes: String?,
    val periodType: BudgetPeriodType?,
    val currency: CurrencyDto,
    val category: TypedCategoryDto,
    val periodProgress: BigDecimal,
    val periodLimit: BudgetPeriodLimitDto,
    // val defaultLimit: BigDecimal,
    // val customLimits: List<BudgetPeriodLimitDto>,
    val accounts: List<AccountDto>,
    @JsonSerialize(using = LocalDateSerializer::class) val startDate: LocalDate,
    @JsonSerialize(using = LocalDateSerializer::class) val endDate: LocalDate?,
) {
    constructor(budget: Budget, periodLimit: BudgetPeriodLimit, periodProgress: BigDecimal): this(
        id = budget.id,
        title = budget.title,
        notes = budget.notes,
        periodType = budget.periodType,
        currency = CurrencyDto(budget.currency),
        category = TypedCategoryDto(budget.category),
        periodProgress = periodProgress,
        periodLimit = BudgetPeriodLimitDto(periodLimit),
        // defaultLimit = budget.defaultLimit,
        // customLimits = budget.customLimits.map { BudgetPeriodLimitDto(it) },
        accounts = budget.accounts.map { AccountDto(it) },
        startDate = budget.startDate,
        endDate = budget.endDate,
    )
}