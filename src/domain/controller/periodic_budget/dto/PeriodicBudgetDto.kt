package io.ducket.api.domain.controller.periodic_budget.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.app.PeriodicBudgetType
import io.ducket.api.utils.LocalDateSerializer
import io.ducket.api.domain.controller.account.dto.AccountDto
import io.ducket.api.domain.controller.category.dto.CategoryDto
import io.ducket.api.domain.controller.currency.dto.CurrencyDto
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PeriodicBudgetDto(
    val id: Long,
    val title: String,
    val notes: String?,
    val currency: CurrencyDto,
    val category: CategoryDto,
    val periodType: PeriodicBudgetType?,
    val accounts: List<AccountDto>,
    @JsonSerialize(using = LocalDateSerializer::class) val startDate: LocalDate,
    @JsonSerialize(using = LocalDateSerializer::class) val closeDate: LocalDate?,
)