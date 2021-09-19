package io.budgery.api.domain.controller.budget

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.budgery.api.InstantSerializer
import io.budgery.api.domain.controller.account.AccountDto
import io.budgery.api.domain.controller.account.CurrencyDto
import io.budgery.api.domain.controller.category.TypelessCategoryDto
import io.budgery.api.domain.model.budget.Budget
import java.math.BigDecimal
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
class BudgetDto(@JsonIgnore val budget: Budget,
                @JsonIgnore val progressDto: BudgetProgressDto,
                @JsonIgnore val periodDto: BudgetPeriodDto? = null,
) {
    val id: Int = budget.id
    val isClosed: Boolean = budget.isClosed
    val period: BudgetPeriodDto? = periodDto
    val name: String = budget.name
    val amount: BigDecimal = budget.limit
    val currency: CurrencyDto = CurrencyDto(budget.currency)
    val progress: BudgetProgressDto = progressDto
    val account: List<AccountDto> = budget.accounts.map { AccountDto(it) }
    val category: TypelessCategoryDto = TypelessCategoryDto(budget.category)
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant = budget.createdAt
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant = budget.modifiedAt
}