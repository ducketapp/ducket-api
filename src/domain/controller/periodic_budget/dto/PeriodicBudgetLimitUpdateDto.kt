package dev.ducket.api.domain.controller.periodic_budget.dto

import dev.ducket.api.app.DEFAULT_SCALE
import dev.ducket.api.utils.scaleBetween
import org.valiktor.functions.isNotZero
import org.valiktor.functions.isPositive
import java.math.BigDecimal

data class PeriodicBudgetLimitUpdateDto(
    val limit: BigDecimal,
) {
    fun validate(): PeriodicBudgetLimitUpdateDto {
        org.valiktor.validate(this) {
            validate(PeriodicBudgetLimitUpdateDto::limit).isNotZero().isPositive().scaleBetween(0, DEFAULT_SCALE)
        }
        return this
    }
}