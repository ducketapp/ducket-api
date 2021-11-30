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
import io.ducket.api.domain.model.StringIdTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.math.BigDecimal
import java.time.Instant

internal object BudgetsTable : StringIdTable("budget") {
    val userId = reference("user_id", UsersTable)
    val currencyId = reference("currency_id", CurrenciesTable)
    val categoryId = reference("category_id", CategoriesTable)
    val periodType = enumerationByName("period_type", 32, BudgetPeriodType::class)
    val name = varchar("name", 45)
    val limit = decimal("limit", 10, 2)
    val isClosed = bool("is_closed")
    val notes = varchar("notes", 128).nullable()
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class BudgetEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, BudgetEntity>(BudgetsTable)

    var user by UserEntity referencedOn BudgetsTable.userId
    var currency by CurrencyEntity referencedOn BudgetsTable.currencyId
    var category by CategoryEntity referencedOn BudgetsTable.categoryId

    var periodType by BudgetsTable.periodType
    var name by BudgetsTable.name
    var limit by BudgetsTable.limit
    var isClosed by BudgetsTable.isClosed
    var notes by BudgetsTable.notes
    var createdAt by BudgetsTable.createdAt
    var modifiedAt by BudgetsTable.modifiedAt

    var accounts by AccountEntity via BudgetAccountsTable

    fun toModel() = Budget(
        id.value,
        accounts.map { it.toModel() },
        category.toModel(),
        currency.toModel(),
        user.toModel(),
        periodType,
        name,
        limit,
        isClosed,
        notes,
        createdAt,
        modifiedAt
    )
}

data class Budget(
    val id: String,
    val accounts: List<Account>,
    val category: Category,
    val currency: Currency,
    val user: User,
    val periodType: BudgetPeriodType,
    val name: String,
    val limit: BigDecimal,
    val isClosed: Boolean,
    val notes: String?,
    val createdAt: Instant,
    val modifiedAt: Instant,
)

enum class BudgetPeriodType {
    WEEKLY, MONTHLY, ANNUAL
}
