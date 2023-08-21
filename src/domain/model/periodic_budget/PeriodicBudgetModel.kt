package org.expenny.service.domain.model.periodic_budget

import org.expenny.service.app.PeriodicBudgetType
import org.expenny.service.domain.model.account.Account
import org.expenny.service.domain.model.account.AccountEntity
import org.expenny.service.domain.model.category.CategoriesTable
import org.expenny.service.domain.model.category.Category
import org.expenny.service.domain.model.category.CategoryEntity
import org.expenny.service.domain.model.currency.CurrenciesTable
import org.expenny.service.domain.model.currency.Currency
import org.expenny.service.domain.model.currency.CurrencyEntity
import org.expenny.service.domain.model.user.User
import org.expenny.service.domain.model.user.UserEntity
import org.expenny.service.domain.model.user.UsersTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.time.LocalDate

internal object PeriodicBudgetsTable : LongIdTable("periodic_budget") {
    val userId = reference("user_id", UsersTable)
    val currencyId = reference("currency_id", CurrenciesTable)
    val categoryId = reference("category_id", CategoriesTable)
    val title = varchar("title", 32)
    val periodType = enumerationByName("period_type", 32, PeriodicBudgetType::class)
    val startDate = date("start_date")
    val closeDate = date("close_date").nullable()
    val notes = varchar("notes", 128).nullable()
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val modifiedAt = timestamp("modified_at").clientDefault { Instant.now() }
}

class PeriodicBudgetEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PeriodicBudgetEntity>(PeriodicBudgetsTable)

    var user by UserEntity referencedOn PeriodicBudgetsTable.userId
    var currency by CurrencyEntity referencedOn PeriodicBudgetsTable.currencyId
    var category by CategoryEntity referencedOn PeriodicBudgetsTable.categoryId

    var periodType by PeriodicBudgetsTable.periodType
    var startDate by PeriodicBudgetsTable.startDate
    var closeDate by PeriodicBudgetsTable.closeDate
    var title by PeriodicBudgetsTable.title
    var notes by PeriodicBudgetsTable.notes
    var createdAt by PeriodicBudgetsTable.createdAt
    var modifiedAt by PeriodicBudgetsTable.modifiedAt

    val accounts by AccountEntity via PeriodicBudgetAccountsTable

    fun toModel() = PeriodicBudget(
        id = id.value,
        title = title,
        user = user.toModel(),
        category = category.toModel(),
        currency = currency.toModel(),
        periodType = periodType,
        startDate = startDate,
        closeDate = closeDate,
        notes = notes,
        accounts = accounts.map { it.toModel() },
        createdAt = createdAt,
        modifiedAt = modifiedAt
    )
}

data class PeriodicBudget(
    val id: Long,
    val title: String,
    val user: User,
    val category: Category,
    val currency: Currency,
    val periodType: PeriodicBudgetType,
    val startDate: LocalDate,
    val closeDate: LocalDate?,
    val notes: String?,
    val accounts: List<Account>,
    val createdAt: Instant,
    val modifiedAt: Instant,
)

data class PeriodicBudgetCreate(
    val userId: Long,
    val title: String,
    val currency: String,
    val categoryId: Long,
    val periodType: PeriodicBudgetType,
    val startDate: LocalDate,
    val notes: String?,
)

data class PeriodicBudgetUpdate(
    val title: String,
    val currency: String,
    val categoryId: Long,
    val periodType: PeriodicBudgetType,
    val startDate: LocalDate,
    val closeDate: LocalDate?,
    val notes: String?,
)
