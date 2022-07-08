package domain.mapper

import domain.model.account.Account
import domain.model.account.AccountCreate
import domain.model.account.AccountUpdate
import domain.model.currency.Currency
import io.ducket.api.domain.controller.account.dto.AccountCreateDto
import io.ducket.api.domain.controller.account.dto.AccountDto
import io.ducket.api.domain.controller.account.dto.AccountUpdateDto
import io.ducket.api.domain.controller.currency.dto.CurrencyDto

object AccountMapper {

    fun mapDtoToModel(dto: AccountCreateDto, userId: Long): AccountCreate {
        return DataClassMapper<AccountCreateDto, AccountCreate>()
            .provide(AccountCreate::userId, userId)
            .invoke(dto)
    }

    fun mapDtoToModel(dto: AccountUpdateDto): AccountUpdate {
        return DataClassMapper<AccountUpdateDto, AccountUpdate>().invoke(dto)
    }

    fun mapModelToDto(model: Account): AccountDto {
        return DataClassMapper<Account, AccountDto>()
            .register(AccountDto::currency, DataClassMapper<Currency, CurrencyDto>())
            .invoke(model)
    }
}