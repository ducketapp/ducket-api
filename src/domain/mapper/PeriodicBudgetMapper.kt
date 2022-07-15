package io.ducket.api.domain.mapper

import io.ducket.api.domain.model.account.Account
import io.ducket.api.domain.model.category.Category
import io.ducket.api.domain.model.currency.Currency
import io.ducket.api.domain.controller.category.dto.CategoryDto
import io.ducket.api.domain.controller.currency.dto.CurrencyDto
import io.ducket.api.domain.model.periodic_budget.PeriodicBudget
import io.ducket.api.domain.model.periodic_budget.PeriodicBudgetCreate
import io.ducket.api.domain.model.periodic_budget.PeriodicBudgetUpdate
import io.ducket.api.domain.controller.account.dto.AccountDto
import io.ducket.api.domain.controller.periodic_budget.dto.PeriodicBudgetCreateDto
import io.ducket.api.domain.controller.periodic_budget.dto.PeriodicBudgetDto
import io.ducket.api.domain.controller.periodic_budget.dto.PeriodicBudgetUpdateDto
import io.ducket.api.domain.mapper.DataClassMapper.Companion.collectionMapper

object PeriodicBudgetMapper {

    fun mapDtoToModel(dto: PeriodicBudgetCreateDto, userId: Long): PeriodicBudgetCreate {
        return DataClassMapper<PeriodicBudgetCreateDto, PeriodicBudgetCreate>()
            .provide(PeriodicBudgetCreate::userId, userId)
            .invoke(dto)
    }

    fun mapDtoToModel(dto: PeriodicBudgetUpdateDto): PeriodicBudgetUpdate {
        return DataClassMapper<PeriodicBudgetUpdateDto, PeriodicBudgetUpdate>().invoke(dto)
    }

    fun mapModelToDto(model: PeriodicBudget): PeriodicBudgetDto {
        val accountMapper = DataClassMapper<Account, AccountDto>()
            .register(AccountDto::currency, DataClassMapper<Currency, CurrencyDto>())

        return DataClassMapper<PeriodicBudget, PeriodicBudgetDto>()
            .register(PeriodicBudgetDto::currency, DataClassMapper<Currency, CurrencyDto>())
            .register(PeriodicBudgetDto::category, DataClassMapper<Category, CategoryDto>())
            .register(PeriodicBudgetDto::accounts, collectionMapper(accountMapper))
            .invoke(model)
    }
}