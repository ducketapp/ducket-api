package domain.mapper

import domain.model.account.Account
import domain.model.category.Category
import domain.model.currency.Currency
import domain.model.periodic_budget.PeriodicBudgetCreate
import io.ducket.api.domain.controller.account.dto.AccountDto
import io.ducket.api.domain.controller.budget.dto.BudgetCreateDto
import io.ducket.api.domain.controller.budget.dto.BudgetDto
import io.ducket.api.domain.controller.budget.dto.BudgetUpdateDto
import io.ducket.api.domain.controller.category.dto.CategoryDto
import io.ducket.api.domain.controller.currency.dto.CurrencyDto
import io.ducket.api.domain.model.budget.Budget
import io.ducket.api.domain.model.budget.BudgetCreate
import io.ducket.api.domain.model.budget.BudgetUpdate

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