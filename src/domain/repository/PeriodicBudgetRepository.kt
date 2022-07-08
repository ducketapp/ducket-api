package io.ducket.api.domain.repository

import domain.model.category.CategoryEntity
import domain.model.currency.CurrenciesTable
import domain.model.currency.CurrencyEntity
import domain.model.periodic_budget.*
import domain.model.periodic_budget.PeriodicBudgetsTable
import domain.model.user.UserEntity
import io.ducket.api.app.database.Transactional
import io.ducket.api.domain.model.budget.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class PeriodicBudgetRepository: Transactional {

    suspend fun create(data: PeriodicBudgetCreate): PeriodicBudget = blockingTransaction {
        PeriodicBudgetEntity.new {
            this.periodType = data.periodType
            this.currency = CurrencyEntity.find { CurrenciesTable.isoCode.eq(data.currency) }.first()
            this.category = CategoryEntity[data.categoryId]
            this.user = UserEntity[data.userId]
            this.title = data.title
            this.startDate = data.startDate
            this.closeDate = null
        }.toModel()
    }

    suspend fun update(userId: Long, budgetId: Long, data: PeriodicBudgetUpdate): PeriodicBudget? = blockingTransaction {
        PeriodicBudgetEntity.find {
            PeriodicBudgetsTable.userId.eq(userId).and(PeriodicBudgetsTable.id.eq(budgetId))
        }.firstOrNull()?.apply {
            this.periodType = data.periodType
            this.currency = CurrencyEntity.find { CurrenciesTable.isoCode.eq(data.currency) }.first()
            this.category = CategoryEntity[data.categoryId]
            this.title = data.title
            this.startDate = data.startDate
            this.closeDate = data.closeDate
        }?.toModel()
    }

    suspend fun findOne(userId: Long, budgetId: Long): PeriodicBudget? = blockingTransaction {
        PeriodicBudgetEntity.find {
            PeriodicBudgetsTable.userId.eq(userId).and(PeriodicBudgetsTable.id.eq(budgetId))
        }.firstOrNull()?.toModel()
    }

    suspend fun findOneByTitle(userId: Long, title: String): PeriodicBudget? = blockingTransaction {
        PeriodicBudgetEntity.find {
            PeriodicBudgetsTable.userId.eq(userId).and(PeriodicBudgetsTable.title.eq(title))
        }.firstOrNull()?.toModel()
    }

    suspend fun findAll(vararg userIds: Long): List<PeriodicBudget> = blockingTransaction {
        PeriodicBudgetEntity.find {
            PeriodicBudgetsTable.userId.inList(userIds.asList())
        }.toList().map { it.toModel() }
    }

    suspend fun delete(userId: Long, vararg budgetIds: Long) = blockingTransaction {
        PeriodicBudgetsTable.deleteWhere {
            PeriodicBudgetsTable.id.inList(budgetIds.asList()).and(PeriodicBudgetsTable.userId.eq(userId))
        }
    }
}