package io.ducket.api.domain.controller.budget

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.ducket.api.InstantSerializer
import io.ducket.api.domain.controller.account.AccountDto
import io.ducket.api.domain.controller.account.CurrencyDto
import io.ducket.api.domain.controller.category.TypelessCategoryDto
import io.ducket.api.domain.controller.user.UserDto
import io.ducket.api.domain.model.budget.Budget
import java.math.BigDecimal
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BudgetDto(
    @JsonIgnore val budget: Budget,
    @JsonIgnore val progressDto: BudgetProgressDto,
    @JsonIgnore val periodDto: BudgetPeriodBoundsDto? = null,
) {
    val id: String = budget.id
    val isClosed: Boolean = budget.isClosed
    val period: BudgetPeriodBoundsDto? = periodDto
    val name: String = budget.name
    val amount: BigDecimal = budget.limit
    val owner: UserDto = UserDto(budget.user)
    val currency: CurrencyDto = CurrencyDto(budget.currency)
    val progress: BudgetProgressDto = progressDto
    val account: List<AccountDto> = budget.accounts.map { AccountDto(it) }
    val category: TypelessCategoryDto = TypelessCategoryDto(budget.category)
    val notes: String? = budget.notes
    @JsonSerialize(using = InstantSerializer::class) val createdAt: Instant = budget.createdAt
    @JsonSerialize(using = InstantSerializer::class) val modifiedAt: Instant = budget.modifiedAt
}