package io.ducket.api.domain.controller.budget

import io.ducket.api.app.BudgetPeriodType
import org.valiktor.functions.*
import java.math.BigDecimal


data class BudgetCreateDto(
    val limit: BigDecimal,
    val name: String,
    val currencyIsoCode: String,
    val budgetPeriod: BudgetPeriodType,
    val accountIds: List<Long>,
    val categoryId: Long,
) {
    fun validate(): BudgetCreateDto {
        org.valiktor.validate(this) {
            validate(BudgetCreateDto::limit).isNotEqualTo(BigDecimal.ZERO).isPositive()
            validate(BudgetCreateDto::name).isNotBlank().hasSize(1, 45)
            validate(BudgetCreateDto::currencyIsoCode).isNotBlank().hasSize(3)
            validate(BudgetCreateDto::budgetPeriod).isNotNull().isIn(BudgetPeriodType.values().toList())
            validate(BudgetCreateDto::accountIds).isNotNull().isNotEmpty()
            validate(BudgetCreateDto::categoryId).isNotZero().isPositive()
        }
        return this
    }
}