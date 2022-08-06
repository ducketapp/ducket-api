package dev.ducketapp.service.domain.controller.account.dto

import com.fasterxml.jackson.annotation.JsonInclude
import dev.ducketapp.service.domain.model.account.Account
import dev.ducketapp.service.app.AccountType
import dev.ducketapp.service.domain.controller.currency.dto.CurrencyDto
import dev.ducketapp.service.domain.controller.user.dto.UserDto
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountDto(
    val id: Long,
    val extId: String?,
    val name: String,
    val startBalance: BigDecimal,
    val totalBalance: BigDecimal,
    val type: AccountType,
    val notes: String?,
    val currency: CurrencyDto,
)
