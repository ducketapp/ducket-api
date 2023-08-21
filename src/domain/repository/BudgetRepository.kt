package org.expenny.service.domain.repository

import org.expenny.service.domain.model.category.CategoryEntity
import org.expenny.service.domain.model.currency.CurrenciesTable
import org.expenny.service.domain.model.currency.CurrencyEntity
import org.expenny.service.domain.model.user.UserEntity
import org.expenny.service.app.database.Transactional
import org.expenny.service.domain.model.budget.*
import org.expenny.service.domain.model.budget.BudgetsTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere

class BudgetRepository: Transactional {

    suspend fun createBudget(data: BudgetCreate): Budget = blockingTransaction {
        BudgetEntity.new {
            this.currency = CurrencyEntity.find { CurrenciesTable.isoCode.eq(data.currency) }.first()
            this.category = CategoryEntity[data.categoryId]
            this.user = UserEntity[data.userId]
            this.title = data.title
            this.limit = data.limit
            this.startDate = data.startDate
            this.endDate = data.endDate
        }.toModel()
    }

    suspend fun updateBudget(userId: Long, budgetId: Long, data: BudgetUpdate): Budget? = blockingTransaction {
        BudgetEntity.find {
            BudgetsTable.userId.eq(userId).and(BudgetsTable.id.eq(budgetId))
        }.firstOrNull()?.apply {
            this.currency = CurrencyEntity.find { CurrenciesTable.isoCode.eq(data.currency) }.first()
            this.category = CategoryEntity[data.categoryId]
            this.title = data.title
            this.limit = data.limit
            this.startDate = data.startDate
            this.endDate = data.endDate
        }?.toModel()
    }

    suspend fun findAll(userId: Long): List<Budget> = blockingTransaction {
        BudgetEntity.find {
            BudgetsTable.userId.eq(userId)
        }.toList().map { it.toModel() }
    }

    suspend fun findOne(userId: Long, budgetId: Long): Budget? = blockingTransaction {
        BudgetEntity.find {
            BudgetsTable.userId.eq(userId).and(BudgetsTable.id.eq(budgetId))
        }.firstOrNull()?.toModel()
    }

    suspend fun findOneByTitle(userId: Long, title: String): Budget? = blockingTransaction {
        BudgetEntity.find {
            BudgetsTable.userId.eq(userId).and(BudgetsTable.title.eq(title))
        }.firstOrNull()?.toModel()
    }

    suspend fun delete(userId: Long, vararg budgetIds: Long) = blockingTransaction {
        BudgetsTable.deleteWhere {
            BudgetsTable.id.inList(budgetIds.asList()).and(BudgetsTable.userId.eq(userId))
        }
    }
}