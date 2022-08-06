package dev.ducket.api.domain.mapper

import dev.ducket.api.domain.model.account.Account
import dev.ducket.api.domain.model.account.AccountCreate
import dev.ducket.api.domain.model.account.AccountUpdate
import dev.ducket.api.domain.model.currency.Currency
import dev.ducket.api.domain.controller.account.dto.AccountCreateDto
import dev.ducket.api.domain.controller.account.dto.AccountDto
import dev.ducket.api.domain.controller.account.dto.AccountUpdateDto
import dev.ducket.api.domain.controller.currency.dto.CurrencyDto
import dev.ducket.api.domain.controller.user.dto.UserDto
import dev.ducket.api.domain.model.user.User

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