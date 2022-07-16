package dev.ducket.api.domain.mapper

import dev.ducket.api.domain.model.account.Account
import dev.ducket.api.domain.model.category.Category
import dev.ducket.api.domain.model.currency.Currency
import dev.ducket.api.domain.controller.category.dto.CategoryDto
import dev.ducket.api.domain.controller.currency.dto.CurrencyDto
import dev.ducket.api.domain.model.periodic_budget.PeriodicBudget
import dev.ducket.api.domain.model.periodic_budget.PeriodicBudgetCreate
import dev.ducket.api.domain.model.periodic_budget.PeriodicBudgetUpdate
import dev.ducket.api.domain.controller.account.dto.AccountDto
import dev.ducket.api.domain.controller.periodic_budget.dto.PeriodicBudgetCreateDto
import dev.ducket.api.domain.controller.periodic_budget.dto.PeriodicBudgetDto
import dev.ducket.api.domain.controller.periodic_budget.dto.PeriodicBudgetUpdateDto
import dev.ducket.api.domain.mapper.DataClassMapper.Companion.collectionMapper

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