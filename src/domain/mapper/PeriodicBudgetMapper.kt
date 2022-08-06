package dev.ducketapp.service.domain.mapper

import dev.ducketapp.service.domain.model.account.Account
import dev.ducketapp.service.domain.model.category.Category
import dev.ducketapp.service.domain.model.currency.Currency
import dev.ducketapp.service.domain.controller.category.dto.CategoryDto
import dev.ducketapp.service.domain.controller.currency.dto.CurrencyDto
import dev.ducketapp.service.domain.model.periodic_budget.PeriodicBudget
import dev.ducketapp.service.domain.model.periodic_budget.PeriodicBudgetCreate
import dev.ducketapp.service.domain.model.periodic_budget.PeriodicBudgetUpdate
import dev.ducketapp.service.domain.controller.account.dto.AccountDto
import dev.ducketapp.service.domain.controller.periodic_budget.dto.PeriodicBudgetCreateDto
import dev.ducketapp.service.domain.controller.periodic_budget.dto.PeriodicBudgetDto
import dev.ducketapp.service.domain.controller.periodic_budget.dto.PeriodicBudgetUpdateDto
import dev.ducketapp.service.domain.mapper.DataClassMapper.Companion.collectionMapper

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