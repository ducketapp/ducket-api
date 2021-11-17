package io.ducket.api.domain.controller.budget

import io.ducket.api.domain.model.budget.BudgetPeriodType
import org.valiktor.functions.*
import java.math.BigDecimal


class BudgetCreateDto(
    val limit: BigDecimal,
    val name: String,
    val currencyIsoCode: String,
    val budgetPeriod: BudgetPeriodType,
    val accountIds: List<String>,
    val categoryId: String,
) {
    fun validate(): BudgetCreateDto {
        org.valiktor.validate(this) {
            validate(BudgetCreateDto::limit).isNotEqualTo(BigDecimal.ZERO).isPositive()
            validate(BudgetCreateDto::name).isNotBlank().hasSize(1, 45)
            validate(BudgetCreateDto::currencyIsoCode).isNotBlank().hasSize(3)
            validate(BudgetCreateDto::budgetPeriod).isNotNull().isIn(BudgetPeriodType.values().toList())
            validate(BudgetCreateDto::accountIds).isNotNull().isNotEmpty()
            validate(BudgetCreateDto::categoryId).isNotNull()
        }
        return this
    }
}