package io.ducket.api.domain.controller.user

import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.ducket.api.domain.controller.account.AccountDto
import io.ducket.api.domain.controller.budget.BudgetDto

class UserDetailsDto(
    @JsonUnwrapped val user: UserDto,
    val accounts: List<AccountDto> = emptyList(),
    val budgets: List<BudgetDto> = emptyList(),
)