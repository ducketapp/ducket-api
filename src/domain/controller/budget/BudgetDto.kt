package io.budgery.api.domain.controller.budget

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.budgery.api.InstantSerializer
import io.budgery.api.LocalDateSerializer
import io.budgery.api.domain.controller.account.AccountDto
import io.budgery.api.domain.controller.category.CategoryDto
import io.budgery.api.domain.model.budget.Budget
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

class BudgetDto(@JsonIgnore val budget: Budget, @JsonIgnore val budgetProgressDto: BudgetProgressDto) {
    val id: Int = budget.id
    val account: List<AccountDto> = budget.accounts.map { AccountDto(it) }
    val categories: List<CategoryDto> = budget.categories.map { CategoryDto(it) }
    val name: String = budget.name
    val amount: BigDecimal = budget.amount
    val progress: BudgetProgressDto = budgetProgressDto
    val isClosed: Boolean = budget.isClosed
    val isCompleted: Boolean = budget.endDate?.isBefore(LocalDate.now()) ?: false
    @JsonSerialize(using = LocalDateSerializer::class) val startDate: LocalDate? = budget.startDate
    @JsonSerialize(using = LocalDateSerializer::class) val endDate: LocalDate? = budget.endDate
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant = budget.createdAt
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant = budget.modifiedAt
}