package org.expenny.service.domain.repository

import org.expenny.service.app.database.Transactional
import org.expenny.service.domain.model.periodic_budget.PeriodicBudgetAccountsTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert

class PeriodicBudgetAccountRepository: Transactional {

    suspend fun create(budgetId: Long, vararg accountIds: Long) = blockingTransaction {
        accountIds.forEach { accountId ->
            PeriodicBudgetAccountsTable.insert {
                it[this.budgetId] = budgetId
                it[this.accountId] = accountId
            }
        }
    }

    suspend fun deleteAllByBudget(budgetId: Long): Unit = blockingTransaction {
        PeriodicBudgetAccountsTable.deleteWhere {
            PeriodicBudgetAccountsTable.budgetId.eq(budgetId)
        }
    }

    suspend fun delete(budgetId: Long, vararg accountIds: Long): Unit = blockingTransaction {
        PeriodicBudgetAccountsTable.deleteWhere {
            PeriodicBudgetAccountsTable.budgetId.eq(budgetId).and(PeriodicBudgetAccountsTable.accountId.inList(accountIds.asList()))
        }
    }
}