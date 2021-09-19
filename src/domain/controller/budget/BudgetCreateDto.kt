package io.budgery.api.domain.controller.budget

import org.valiktor.functions.*
import java.math.BigDecimal


class BudgetCreateDto(
    val limit: BigDecimal,
    val name: String,
    val currencyIsoCode: String,
    val budgetPeriod: String,
    val accountIds: List<Int>,
    val categoryId: Int,
) {
    fun validate(): BudgetCreateDto {
        org.valiktor.validate(this) {
            validate(BudgetCreateDto::limit).isNotEqualTo(BigDecimal.ZERO).isPositive()
            validate(BudgetCreateDto::name).isNotBlank().hasSize(1, 45)
            validate(BudgetCreateDto::currencyIsoCode).isNotBlank().hasSize(3)
            validate(BudgetCreateDto::budgetPeriod).isNotBlank()
            validate(BudgetCreateDto::accountIds).isNotNull().isNotEmpty()
            validate(BudgetCreateDto::categoryId).isNotNull()
/*            validate(BudgetCreateDto::startDay).isNotNull()
            validate(BudgetCreateDto::endDay).isNotNull()*/

            /*if (startDay.isAfter(endDay) || endDay.isBefore(LocalDate.now())) {
                throw IllegalArgumentException("Invalid period boundaries")
            }

            if (budgetPeriod != null) {
                val calculatedBounds = budgetPeriod.getBounds()

                if (calculatedBounds.first != startDay || calculatedBounds.second != endDay) {
                    throw IllegalArgumentException("Invalid period boundaries for ${budgetPeriod.name.toLowerCase()} period")
                }
            }*/
        }
        return this
    }
}