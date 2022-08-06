package dev.ducketapp.service.domain.controller.budget.dto

import com.fasterxml.jackson.annotation.JsonInclude
import dev.ducketapp.service.domain.controller.account.dto.AccountDto
import dev.ducketapp.service.domain.controller.category.dto.CategoryDto
import dev.ducketapp.service.domain.controller.currency.dto.CurrencyDto
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BudgetDto(
    val id: Long,
    val title: String,
    val notes: String?,
    val currency: CurrencyDto,
    val category: CategoryDto,
    val accounts: List<AccountDto>,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
