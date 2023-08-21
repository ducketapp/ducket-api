package org.expenny.service.domain.mapper

import org.expenny.service.domain.model.account.Account
import org.expenny.service.domain.model.category.Category
import org.expenny.service.domain.model.currency.Currency
import org.expenny.service.domain.controller.category.dto.CategoryDto
import org.expenny.service.domain.controller.currency.dto.CurrencyDto
import org.expenny.service.domain.model.periodic_budget.PeriodicBudget
import org.expenny.service.domain.model.periodic_budget.PeriodicBudgetCreate
import org.expenny.service.domain.model.periodic_budget.PeriodicBudgetUpdate
import org.expenny.service.domain.controller.account.dto.AccountDto
import org.expenny.service.domain.controller.periodic_budget.dto.PeriodicBudgetCreateDto
import org.expenny.service.domain.controller.periodic_budget.dto.PeriodicBudgetDto
import org.expenny.service.domain.controller.periodic_budget.dto.PeriodicBudgetUpdateDto
import org.expenny.service.domain.mapper.DataClassMapper.Companion.collectionMapper

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