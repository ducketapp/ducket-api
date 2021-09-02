package io.budgery.api.domain.controller.user

import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.budgery.api.domain.controller.imports.ImportDto
import io.budgery.api.domain.controller.account.AccountDto

class UserDetailsDto(
    @JsonUnwrapped val user: UserDto,
    val accounts: List<AccountDto> = emptyList(),
    val imports: List<ImportDto> = emptyList(),
)