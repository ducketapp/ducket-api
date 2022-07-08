package io.ducket.api.domain.controller.budget.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.domain.controller.account.dto.AccountDto
import io.ducket.api.domain.controller.category.dto.CategoryDto
import io.ducket.api.domain.controller.currency.dto.CurrencyDto
import io.ducket.api.utils.LocalDateSerializer
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BudgetDto(
    val id: Long,
    val title: String,
    val notes: String?,
    val currency: CurrencyDto,
    val category: CategoryDto,
    val accounts: List<AccountDto>,
    @JsonSerialize(using = LocalDateSerializer::class) val startDate: LocalDate,
    @JsonSerialize(using = LocalDateSerializer::class) val endDate: LocalDate,
)
