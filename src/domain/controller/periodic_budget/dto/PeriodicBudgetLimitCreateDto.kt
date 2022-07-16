package dev.ducket.api.domain.controller.periodic_budget.dto

import dev.ducket.api.app.DEFAULT_SCALE
import dev.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.LocalDate

data class PeriodicBudgetLimitCreateDto(
    val limit: BigDecimal,
    val fromDate: LocalDate,
    val toDate: LocalDate,
) {
    fun validate(): PeriodicBudgetLimitCreateDto {
        org.valiktor.validate(this) {
            validate(PeriodicBudgetLimitCreateDto::limit).isNotZero().isPositive().scaleBetween(0, DEFAULT_SCALE)
        }
        return this
    }
}
