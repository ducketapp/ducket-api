package io.ducket.api.domain.repository

import domain.model.periodic_budget.*
import domain.model.periodic_budget.PeriodicBudgetLimitsTable
import io.ducket.api.app.database.Transactional
import io.ducket.api.domain.model.budget.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.select
import java.time.LocalDate

class PeriodicBudgetLimitRepository: Transactional {

    suspend fun create(data: PeriodicBudgetLimitCreate): PeriodicBudgetLimit = blockingTransaction {
        PeriodicBudgetLimitEntity.new {
            this.budget = PeriodicBudgetEntity[data.budgetId]
            this.default = data.default
            this.limit = data.limit
            this.fromDate = data.fromDate
            this.toDate = data.toDate
        }.toModel()
    }

    suspend fun update(limitId: Long, data: PeriodicBudgetLimitUpdate): PeriodicBudgetLimit? = blockingTransaction {
        PeriodicBudgetLimitEntity.findById(limitId)?.apply {
            this.limit = data.limit
            this.fromDate = data.fromDate
            this.toDate = data.toDate
        }?.toModel()
    }

    suspend fun findOneByBudgetAndPeriod(budgetId: Long, fromDate: LocalDate, toDate: LocalDate): PeriodicBudgetLimit? = blockingTransaction {
        PeriodicBudgetLimitEntity.find {
            PeriodicBudgetLimitsTable.budgetId.eq(budgetId)
                .and(
                    PeriodicBudgetLimitsTable.fromDate.eq(fromDate)
                    .and(PeriodicBudgetLimitsTable.toDate.eq(toDate)))
        }.firstOrNull()?.toModel()
    }

    suspend fun findAllByBudget(budgetId: Long): List<PeriodicBudgetLimit> = blockingTransaction {
        PeriodicBudgetLimitEntity.find {
            PeriodicBudgetLimitsTable.budgetId.eq(budgetId)
        }.toList().map { it.toModel() }
    }

    suspend fun findOne(limitId: Long): PeriodicBudgetLimit? = blockingTransaction {
        PeriodicBudgetLimitEntity.findById(limitId)?.toModel()
    }

    suspend fun findOneByIdAndBudget(userId: Long, budgetId: Long, limitId: Long): PeriodicBudgetLimit? = blockingTransaction {
        PeriodicBudgetLimitEntity.wrapRows(
            PeriodicBudgetLimitsTable.select {
                PeriodicBudgetLimitsTable.budgetId.eq(budgetId).and(PeriodicBudgetLimitsTable.id.eq(limitId)).and {
                    exists(PeriodicBudgetsTable.select {
                        PeriodicBudgetsTable.userId.eq(userId).and(PeriodicBudgetsTable.id.eq(budgetId))
                    })
                }
            }
        ).firstOrNull()?.toModel()
    }

    suspend fun findDefaultByBudget(budgetId: Long): PeriodicBudgetLimit? = blockingTransaction {
        PeriodicBudgetLimitEntity.find {
            PeriodicBudgetLimitsTable.budgetId.eq(budgetId).and(PeriodicBudgetLimitsTable.default.eq(true))
        }.firstOrNull()?.toModel()
    }
}