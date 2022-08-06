package dev.ducketapp.service.domain.mapper

import dev.ducketapp.service.domain.model.account.Account
import dev.ducketapp.service.domain.model.account.AccountCreate
import dev.ducketapp.service.domain.model.account.AccountUpdate
import dev.ducketapp.service.domain.model.currency.Currency
import dev.ducketapp.service.domain.controller.account.dto.AccountCreateDto
import dev.ducketapp.service.domain.controller.account.dto.AccountDto
import dev.ducketapp.service.domain.controller.account.dto.AccountUpdateDto
import dev.ducketapp.service.domain.controller.currency.dto.CurrencyDto
import dev.ducketapp.service.domain.controller.user.dto.UserDto
import dev.ducketapp.service.domain.model.user.User

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