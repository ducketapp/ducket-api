package org.expenny.service.domain.controller.periodic_budget.dto

import org.expenny.service.app.DEFAULT_SCALE
import org.expenny.service.utils.scaleBetween
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