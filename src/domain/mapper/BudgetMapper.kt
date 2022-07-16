package dev.ducket.api.domain.mapper

import dev.ducket.api.domain.model.account.Account
import dev.ducket.api.domain.model.category.Category
import dev.ducket.api.domain.model.currency.Currency
import dev.ducket.api.domain.model.periodic_budget.PeriodicBudgetCreate
import dev.ducket.api.domain.controller.account.dto.AccountDto
import dev.ducket.api.domain.controller.budget.dto.BudgetCreateDto
import dev.ducket.api.domain.controller.budget.dto.BudgetDto
import dev.ducket.api.domain.controller.budget.dto.BudgetUpdateDto
import dev.ducket.api.domain.controller.category.dto.CategoryDto
import dev.ducket.api.domain.controller.currency.dto.CurrencyDto
import dev.ducket.api.domain.model.budget.Budget
import dev.ducket.api.domain.model.budget.BudgetCreate
import dev.ducket.api.domain.model.budget.BudgetUpdate

object BudgetMapper {

    fun mapDtoToModel(dto: BudgetCreateDto, userId: Long): BudgetCreate {
        return DataClassMapper<BudgetCreateDto, BudgetCreate>()
            .provide(PeriodicBudgetCreate::userId, userId)
            .invoke(dto)
    }

    fun mapDtoToModel(dto: BudgetUpdateDto): BudgetUpdate {
        return DataClassMapper<BudgetUpdateDto, BudgetUpdate>().invoke(dto)
    }

    fun mapModelToDto(model: Budget): BudgetDto {
        val accountMapper = DataClassMapper<Account, AccountDto>()
            .register(AccountDto::currency, DataClassMapper<Currency, CurrencyDto>())

        return DataClassMapper<Budget, BudgetDto>()
            .register(BudgetDto::currency, DataClassMapper<Currency, CurrencyDto>())
            .register(BudgetDto::category, DataClassMapper<Category, CategoryDto>())
            .register(BudgetDto::accounts, DataClassMapper.collectionMapper(accountMapper))
            .invoke(model)
    }
}