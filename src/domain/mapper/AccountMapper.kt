package io.ducket.api.domain.mapper

import io.ducket.api.domain.model.account.Account
import io.ducket.api.domain.model.account.AccountCreate
import io.ducket.api.domain.model.account.AccountUpdate
import io.ducket.api.domain.model.currency.Currency
import io.ducket.api.domain.controller.account.dto.AccountCreateDto
import io.ducket.api.domain.controller.account.dto.AccountDto
import io.ducket.api.domain.controller.account.dto.AccountUpdateDto
import io.ducket.api.domain.controller.currency.dto.CurrencyDto

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