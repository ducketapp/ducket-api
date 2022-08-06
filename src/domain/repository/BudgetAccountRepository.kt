package dev.ducketapp.service.domain.repository

import dev.ducketapp.service.app.database.Transactional
import dev.ducketapp.service.domain.model.budget.BudgetAccountsTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert

class BudgetAccountRepository: Transactional {

    suspend fun create(budgetId: Long, vararg accountIds: Long) = blockingTransaction {
        accountIds.forEach { accountId ->
            BudgetAccountsTable.insert {
                it[this.budgetId] = budgetId
                it[this.accountId] = accountId
            }
        }
    }

    suspend fun deleteAllByBudget(budgetId: Long): Unit = blockingTransaction {
        BudgetAccountsTable.deleteWhere {
            BudgetAccountsTable.budgetId.eq(budgetId)
        }
    }
}