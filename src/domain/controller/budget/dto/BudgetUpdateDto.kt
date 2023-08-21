package org.expenny.service.domain.controller.budget.dto

import org.expenny.service.app.DEFAULT_SCALE
import org.expenny.service.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.LocalDate

data class BudgetUpdateDto(
    val title: String,
    val limit: BigDecimal,
    val notes: String?,
    val currency: String,
    val categoryId: Long,
    val accountIds: List<Long>,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    fun validate(): BudgetUpdateDto {
        org.valiktor.validate(this) {
            validate(BudgetUpdateDto::limit).isNotZero().isPositive().scaleBetween(0, DEFAULT_SCALE)
            validate(BudgetUpdateDto::notes).hasSize(1, 128)
            validate(BudgetUpdateDto::title).isNotBlank().hasSize(1, 32)
            validate(BudgetUpdateDto::currency).isNotBlank().hasSize(3)
            validate(BudgetUpdateDto::accountIds).isNotEmpty()
            validate(BudgetUpdateDto::categoryId).isPositive()
            validate(BudgetUpdateDto::startDate).isLessThanOrEqualTo(endDate)
        }
        return this
    }
}
