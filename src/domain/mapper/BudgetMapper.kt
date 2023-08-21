package org.expenny.service.domain.mapper

import org.expenny.service.domain.model.account.Account
import org.expenny.service.domain.model.category.Category
import org.expenny.service.domain.model.currency.Currency
import org.expenny.service.domain.model.periodic_budget.PeriodicBudgetCreate
import org.expenny.service.domain.controller.account.dto.AccountDto
import org.expenny.service.domain.controller.budget.dto.BudgetCreateDto
import org.expenny.service.domain.controller.budget.dto.BudgetDto
import org.expenny.service.domain.controller.budget.dto.BudgetUpdateDto
import org.expenny.service.domain.controller.category.dto.CategoryDto
import org.expenny.service.domain.controller.currency.dto.CurrencyDto
import org.expenny.service.domain.model.budget.Budget
import org.expenny.service.domain.model.budget.BudgetCreate
import org.expenny.service.domain.model.budget.BudgetUpdate

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