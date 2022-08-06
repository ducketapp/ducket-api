package dev.ducketapp.service.domain.mapper

import dev.ducketapp.service.domain.model.account.Account
import dev.ducketapp.service.domain.model.category.Category
import dev.ducketapp.service.domain.model.currency.Currency
import dev.ducketapp.service.domain.model.periodic_budget.PeriodicBudgetCreate
import dev.ducketapp.service.domain.controller.account.dto.AccountDto
import dev.ducketapp.service.domain.controller.budget.dto.BudgetCreateDto
import dev.ducketapp.service.domain.controller.budget.dto.BudgetDto
import dev.ducketapp.service.domain.controller.budget.dto.BudgetUpdateDto
import dev.ducketapp.service.domain.controller.category.dto.CategoryDto
import dev.ducketapp.service.domain.controller.currency.dto.CurrencyDto
import dev.ducketapp.service.domain.model.budget.Budget
import dev.ducketapp.service.domain.model.budget.BudgetCreate
import dev.ducketapp.service.domain.model.budget.BudgetUpdate

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