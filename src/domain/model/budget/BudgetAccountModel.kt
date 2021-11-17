package io.ducket.api.domain.model.budget

import domain.model.account.Account
import domain.model.account.AccountEntity
import domain.model.account.AccountsTable
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import java.util.*

internal object BudgetAccountsTable : Table("budget_account") {
    val budgetId = reference("budget_id", BudgetsTable)
    val accountId = reference("account_id", AccountsTable)

    override val primaryKey = PrimaryKey(budgetId, accountId)
}

/*
class BudgetAccountEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, BudgetAccountEntity>(BudgetAccountsTable)

    var budget by BudgetEntity referencedOn BudgetAccountsTable.budgetId
    var account by AccountEntity referencedOn BudgetAccountsTable.accountId

    fun toModel() = BudgetAccount(id.value, budget.toModel(), account.toModel())
}

data class BudgetAccount(
    val id: String,
    val budget: Budget,
    val account: Account,
)*/
