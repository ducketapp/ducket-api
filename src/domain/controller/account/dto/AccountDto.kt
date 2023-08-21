package org.expenny.service.domain.controller.account.dto

import com.fasterxml.jackson.annotation.JsonInclude
import org.expenny.service.domain.model.account.Account
import org.expenny.service.app.AccountType
import org.expenny.service.domain.controller.currency.dto.CurrencyDto
import org.expenny.service.domain.controller.user.dto.UserDto
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
