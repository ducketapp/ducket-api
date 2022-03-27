package io.ducket.api.domain.model.budget

import domain.model.account.Account
import domain.model.account.AccountEntity
import domain.model.category.CategoriesTable
import domain.model.category.Category
import domain.model.category.CategoryEntity
import domain.model.currency.CurrenciesTable
import domain.model.currency.Currency
import domain.model.currency.CurrencyEntity
import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

internal object BudgetsTable : LongIdTable("budget") {
    val userId = reference("user_id", UsersTable)
    val currencyId = reference("currency_id", CurrenciesTable)
    val fromDate = date("from_date")
    val toDate = date("to_date")
    val name = varchar("name", 45)
    val limit = decimal("limit", 10, 2)
    val isClosed = bool("is_closed")
    val notes = varchar("notes", 128).nullable()
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class BudgetEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<BudgetEntity>(BudgetsTable)

    var user by UserEntity referencedOn BudgetsTable.userId
    var currency by CurrencyEntity referencedOn BudgetsTable.currencyId

    var fromDate by BudgetsTable.fromDate
    var toDate by BudgetsTable.toDate
    var name by BudgetsTable.name
    var limit by BudgetsTable.limit
    var isClosed by BudgetsTable.isClosed
    var notes by BudgetsTable.notes
    var createdAt by BudgetsTable.createdAt
    var modifiedAt by BudgetsTable.modifiedAt

    var accounts by AccountEntity via BudgetAccountsTable
    var categories by CategoryEntity via BudgetCategoriesTable

    fun toModel() = Budget(
        id.value,
        accounts.map { it.toModel() },
        categories.map { it.toModel() },
        currency.toModel(),
        user.toModel(),
        fromDate,
        toDate,
        name,
        limit,
        isClosed,
        notes,
        createdAt,
        modifiedAt
    )
}

data class Budget(
    val id: Long,
    val accounts: List<Account>,
    val categories: List<Category>,
    val currency: Currency,
    val user: User,
    var fromDate: LocalDate,
    var toDate: LocalDate,
    val name: String,
    val limit: BigDecimal,
    val isClosed: Boolean,
    val notes: String?,
    val createdAt: Instant,
    val modifiedAt: Instant,
)
