package io.budgery.api.domain.repository

import domain.model.account.AccountEntity
import domain.model.category.CategoryEntity
import domain.model.currency.CurrencyEntity
import domain.model.user.UserEntity
import io.budgery.api.domain.controller.budget.BudgetCreateDto
import io.budgery.api.domain.model.budget.*
import io.budgery.api.domain.model.budget.BudgetsTable
import io.budgery.api.getLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class BudgetRepository {
    private val logger = getLogger()

    fun create(userId: Int, periodTypeId: Int, currencyId: Int, newBudgetDto: BudgetCreateDto): Budget = transaction {
        val newBudget = BudgetEntity.new {
            name = newBudgetDto.name
            category = CategoryEntity[newBudgetDto.categoryId]
            currency = CurrencyEntity[currencyId]
            budgetPeriodType = BudgetPeriodTypeEntity[periodTypeId]
            limit = newBudgetDto.limit
            user = UserEntity[userId]
            isClosed = false
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()

        newBudgetDto.accountIds.forEach {
            BudgetAccountEntity.new {
                budget = BudgetEntity[newBudget.id]
                account = AccountEntity[it]
            }
        }

        return@transaction newBudget
    }

    fun findOne(userId: Int, budgetId: Int): Budget? = transaction {
        BudgetEntity.find { BudgetsTable.userId.eq(userId).and(BudgetsTable.id.eq(budgetId)) }.firstOrNull()?.toModel()
    }

    fun findOneByName(userId: Int, name: String): Budget? = transaction {
        BudgetEntity.find { BudgetsTable.userId.eq(userId).and(BudgetsTable.name.eq(name)) }.firstOrNull()?.toModel()
    }

    fun findAll(userId: Int): List<Budget> = transaction {
        BudgetEntity.find { BudgetsTable.userId.eq(userId) }.map { it.toModel() }
    }

    fun all(): List<Budget> = transaction {
        BudgetEntity.all().map { it.toModel() }
    }

    fun findPeriodType(typeId: Int): BudgetPeriodType? = transaction {
        BudgetPeriodTypeEntity.find { BudgetPeriodTypesTable.id.eq(typeId) }.firstOrNull()?.toModel()
    }

    fun findPeriodType(period: String): BudgetPeriodType? = transaction {
        BudgetPeriodTypeEntity.find { BudgetPeriodTypesTable.period.eq(period) }.firstOrNull()?.toModel()
    }
}