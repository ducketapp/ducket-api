package dev.ducket.api.domain.model.periodic_budget

import dev.ducket.api.domain.model.account.AccountsTable
import org.jetbrains.exposed.sql.Table

internal object PeriodicBudgetAccountsTable : Table("periodic_budget_account") {
    val budgetId = reference("budget_id", PeriodicBudgetsTable.id)
    val accountId = reference("account_id", AccountsTable.id)

    override val primaryKey = PrimaryKey(budgetId, accountId)
}
