package io.budgery.api.domain.model.budget

import domain.model.account.Account
import domain.model.account.AccountEntity
import domain.model.account.AccountsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object BudgetAccountsTable : IntIdTable("budget_account") {
    val budgetId = reference("budget_id", BudgetsTable)
    val accountId = reference("account_id", AccountsTable)
}

class BudgetAccountEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<BudgetAccountEntity>(BudgetAccountsTable)

    var budget by BudgetEntity referencedOn BudgetAccountsTable.budgetId
    var account by AccountEntity referencedOn BudgetAccountsTable.accountId

    fun toModel() = BudgetAccount(id.value, budget.toModel(), account.toModel())
}

data class BudgetAccount(
    val id: Int,
    val budget: Budget,
    val account: Account,
)