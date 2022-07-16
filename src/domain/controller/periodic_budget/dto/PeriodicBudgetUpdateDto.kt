package dev.ducket.api.domain.controller.periodic_budget.dto

import dev.ducket.api.app.PeriodicBudgetType
import dev.ducket.api.app.DEFAULT_SCALE
import dev.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.LocalDate

data class PeriodicBudgetUpdateDto(
    val title: String,
    val limit: BigDecimal,
    val periodType: PeriodicBudgetType,
    val notes: String?,
    val currency: String,
    val categoryId: Long,
    val accountIds: List<Long>,
    val startDate: LocalDate,
    val closeDate: LocalDate?,
) {
    fun validate(): PeriodicBudgetUpdateDto {
        org.valiktor.validate(this) {
            validate(PeriodicBudgetUpdateDto::limit).isNotZero().isPositive().scaleBetween(0, DEFAULT_SCALE)
            validate(PeriodicBudgetUpdateDto::notes).hasSize(1, 128)
            validate(PeriodicBudgetUpdateDto::title).isNotBlank().hasSize(1, 32)
            validate(PeriodicBudgetUpdateDto::currency).isNotBlank().hasSize(3)
            validate(PeriodicBudgetUpdateDto::accountIds).isNotEmpty()
            validate(PeriodicBudgetUpdateDto::categoryId).isPositive()
            validate(PeriodicBudgetUpdateDto::closeDate).isGreaterThanOrEqualTo(startDate)
        }
        return this
    }
}
