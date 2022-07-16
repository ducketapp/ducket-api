package dev.ducket.api.domain.controller.periodic_budget.dto

import dev.ducket.api.app.PeriodicBudgetType
import dev.ducket.api.app.DEFAULT_SCALE
import dev.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.LocalDate


data class PeriodicBudgetCreateDto(
    val title: String,
    val limit: BigDecimal,
    val periodType: PeriodicBudgetType,
    val notes: String?,
    val currency: String,
    val categoryId: Long,
    val accountIds: List<Long>,
    val startDate: LocalDate,
) {
    fun validate(): PeriodicBudgetCreateDto {
        org.valiktor.validate(this) {
            validate(PeriodicBudgetCreateDto::limit).isNotZero().isPositive().scaleBetween(0, DEFAULT_SCALE)
            validate(PeriodicBudgetCreateDto::notes).hasSize(1, 128)
            validate(PeriodicBudgetCreateDto::title).isNotBlank().hasSize(1, 32)
            validate(PeriodicBudgetCreateDto::currency).isNotBlank().hasSize(3)
            validate(PeriodicBudgetCreateDto::accountIds).isNotEmpty()
            validate(PeriodicBudgetCreateDto::categoryId).isPositive()
        }
        return this
    }
}