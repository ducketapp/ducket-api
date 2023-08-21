package org.expenny.service.domain.mapper

import org.expenny.service.domain.model.account.Account
import org.expenny.service.domain.model.account.AccountCreate
import org.expenny.service.domain.model.account.AccountUpdate
import org.expenny.service.domain.model.currency.Currency
import org.expenny.service.domain.controller.account.dto.AccountCreateDto
import org.expenny.service.domain.controller.account.dto.AccountDto
import org.expenny.service.domain.controller.account.dto.AccountUpdateDto
import org.expenny.service.domain.controller.currency.dto.CurrencyDto
import org.expenny.service.domain.controller.user.dto.UserDto
import org.expenny.service.domain.model.user.User

object AccountMapper {

    fun AccountCreateDto.toModel(userId: Long): AccountCreate {
        return DataClassMapper<AccountCreateDto, AccountCreate>()
            .provide(AccountCreate::userId, userId)
            .invoke(this)
    }

    fun AccountUpdateDto.toModel(): AccountUpdate {
        return DataClassMapper<AccountUpdateDto, AccountUpdate>().invoke(this)
    }

    fun Account.toDto(): AccountDto {
        return DataClassMapper<Account, AccountDto>()
            .register(AccountDto::currency, DataClassMapper<Currency, CurrencyDto>())
            .invoke(this)
    }
}