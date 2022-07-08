package domain.mapper

import domain.model.account.Account
import domain.model.category.Category
import domain.model.currency.Currency
import domain.mapper.DataClassMapper.Companion.collectionMapper
import io.ducket.api.domain.controller.account.dto.AccountDto
import domain.controller.periodic_budget.dto.PeriodicBudgetCreateDto
import domain.controller.periodic_budget.dto.PeriodicBudgetDto
import domain.controller.periodic_budget.dto.PeriodicBudgetUpdateDto
import io.ducket.api.domain.controller.category.dto.CategoryDto
import io.ducket.api.domain.controller.currency.dto.CurrencyDto
import domain.model.periodic_budget.PeriodicBudget
import domain.model.periodic_budget.PeriodicBudgetCreate
import domain.model.periodic_budget.PeriodicBudgetUpdate

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