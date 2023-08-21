package org.expenny.service.domain.controller.budget.dto

import com.fasterxml.jackson.annotation.JsonInclude
import org.expenny.service.domain.controller.account.dto.AccountDto
import org.expenny.service.domain.controller.category.dto.CategoryDto
import org.expenny.service.domain.controller.currency.dto.CurrencyDto
import java.math.BigDecimal
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BudgetDto(
    val id: Long,
    val title: String,
    val limit: BigDecimal,
    val notes: String?,
    val currency: CurrencyDto,
    val category: CategoryDto,
    val accounts: List<AccountDto>,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
