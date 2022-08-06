package dev.ducketapp.service.domain.controller.periodic_budget.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PeriodicBudgetLimitDto(
    val id: Long,
    val default: Boolean,
    val limit: BigDecimal,
    val fromDate: LocalDate,
    val toDate: LocalDate,
)
