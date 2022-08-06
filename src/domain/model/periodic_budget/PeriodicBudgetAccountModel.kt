package dev.ducketapp.service.domain.model.periodic_budget

import dev.ducketapp.service.domain.model.account.AccountsTable
import org.jetbrains.exposed.sql.Table

internal object PeriodicBudgetAccountsTable : Table("periodic_budget_account") {
    val budgetId = reference("budget_id", PeriodicBudgetsTable.id)
    val accountId = reference("account_id", AccountsTable.id)

    override val primaryKey = PrimaryKey(budgetId, accountId)
}
