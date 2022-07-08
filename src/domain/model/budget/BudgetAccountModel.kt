package io.ducket.api.domain.model.budget

import domain.model.account.AccountsTable
import org.jetbrains.exposed.sql.Table

internal object BudgetAccountsTable : Table("budget_account") {
    val budgetId = reference("budget_id", BudgetsTable.id)
    val accountId = reference("account_id", AccountsTable.id)

    override val primaryKey = PrimaryKey(budgetId, accountId)
}