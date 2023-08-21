package org.expenny.service.domain.repository

import org.expenny.service.domain.model.category.CategoryEntity
import org.expenny.service.domain.model.currency.CurrenciesTable
import org.expenny.service.domain.model.currency.CurrencyEntity
import org.expenny.service.domain.model.periodic_budget.*
import org.expenny.service.domain.model.periodic_budget.PeriodicBudgetsTable
import org.expenny.service.domain.model.user.UserEntity
import org.expenny.service.app.database.Transactional
import org.jetbrains.exposed.sql.*

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