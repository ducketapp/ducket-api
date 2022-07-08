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
import io.ducket.api.app.DEFAULT_SCALE
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
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
    val categoryId = reference("category_id", CategoriesTable)
    val title = varchar("title", 32)
    val limit = decimal("limit", 10, DEFAULT_SCALE)
    val startDate = date("start_date")
    val endDate = date("end_date")
    val notes = varchar("notes", 128).nullable()
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val modifiedAt = timestamp("modified_at").clientDefault { Instant.now() }
}

class BudgetEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<BudgetEntity>(BudgetsTable)

    var user by UserEntity referencedOn BudgetsTable.userId
    var currency by CurrencyEntity referencedOn BudgetsTable.currencyId
    var category by CategoryEntity referencedOn BudgetsTable.categoryId

    var startDate by BudgetsTable.startDate
    var endDate by BudgetsTable.endDate
    var title by BudgetsTable.title
    var limit by BudgetsTable.limit
    var notes by BudgetsTable.notes
    var createdAt by BudgetsTable.createdAt
    var modifiedAt by BudgetsTable.modifiedAt

    val accounts by AccountEntity via BudgetAccountsTable

    fun toModel() = Budget(
        id = id.value,
        title = title,
        limit = limit,
        user = user.toModel(),
        category = category.toModel(),
        currency = currency.toModel(),
        startDate = startDate,
        endDate = endDate,
        notes = notes,
        accounts = accounts.map { it.toModel() },
        createdAt = createdAt,
        modifiedAt = modifiedAt
    )
}

data class Budget(
    val id: Long,
    val title: String,
    val limit: BigDecimal,
    val user: User,
    val category: Category,
    val currency: Currency,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val notes: String?,
    val accounts: List<Account>,
    val createdAt: Instant,
    val modifiedAt: Instant,
)

data class BudgetCreate(
    val userId: Long,
    val title: String,
    val limit: BigDecimal,
    val currency: String,
    val categoryId: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val notes: String?,
)

data class BudgetUpdate(
    val title: String,
    val limit: BigDecimal,
    val currency: String,
    val categoryId: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val notes: String?,
)
