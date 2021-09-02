package io.budgery.api.domain.model.budget

import domain.model.account.Account
import domain.model.account.AccountEntity
import domain.model.account.AccountsTable
import domain.model.category.CategoriesTable
import domain.model.category.Category
import domain.model.category.CategoryEntity
import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.budgery.api.domain.model.label.LabelEntity
import io.budgery.api.domain.model.label.TransactionLabelsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.date
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

internal object BudgetsTable : IntIdTable("budget") {
    val userId = reference("user_id", UsersTable)
    val name = varchar("name", 45)
    val amount = decimal("amount", 10, 2)
    val isClosed = bool("is_closed")
    val startDate = date("start_day").nullable()
    val endDate = date("end_day").nullable()
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class BudgetEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<BudgetEntity>(BudgetsTable)

    var user by UserEntity referencedOn BudgetsTable.userId
    var name by BudgetsTable.name
    var amount by BudgetsTable.amount
    var isClosed by BudgetsTable.isClosed
    var startDate by BudgetsTable.startDate
    var endDate by BudgetsTable.endDate
    var createdAt by BudgetsTable.createdAt
    var modifiedAt by BudgetsTable.modifiedAt

    var accounts by AccountEntity via BudgetAccountsTable
    var categories by CategoryEntity via BudgetCategoriesTable

    fun toModel() = Budget(
        id.value,
        accounts.map { it.toModel() },
        categories.map { it.toModel() },
        user.toModel(),
        name,
        amount,
        isClosed,
        startDate,
        endDate,
        createdAt,
        modifiedAt
    )
}

data class Budget(
    val id: Int,
    val accounts: List<Account>,
    val categories: List<Category>,
    val user: User,
    val name: String,
    val amount: BigDecimal,
    val isClosed: Boolean,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val createdAt: Instant,
    val modifiedAt: Instant,
)