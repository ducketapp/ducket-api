package io.ducket.api.domain.controller.budget

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.LocalDateSerializer
import java.time.LocalDate

data class BudgetPeriodBoundsDto(
    @JsonSerialize(using = LocalDateSerializer::class) val lowerBound: LocalDate,
    @JsonSerialize(using = LocalDateSerializer::class) val upperBound: LocalDate,
)