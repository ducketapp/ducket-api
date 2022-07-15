package io.ducket.api.domain.controller.periodic_budget.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.ducket.api.app.DEFAULT_SCALE
import io.ducket.api.utils.LocalDateDeserializer
import io.ducket.api.utils.scaleBetween
import org.valiktor.functions.*
import java.math.BigDecimal
import java.time.LocalDate

data class PeriodicBudgetLimitCreateDto(
    val limit: BigDecimal,
    @JsonDeserialize(using = LocalDateDeserializer::class) val fromDate: LocalDate,
    @JsonDeserialize(using = LocalDateDeserializer::class) val toDate: LocalDate,
) {
    fun validate(): PeriodicBudgetLimitCreateDto {
        org.valiktor.validate(this) {
            validate(PeriodicBudgetLimitCreateDto::limit).isNotZero().isPositive().scaleBetween(0, DEFAULT_SCALE)
        }
        return this
    }
}
