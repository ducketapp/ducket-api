package domain.model.transaction

import com.sun.org.apache.xpath.internal.operations.Bool
import domain.model.category.CategoriesTable
import domain.model.category.Category
import domain.model.category.CategoryEntity
import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

internal object TransactionRulesTable : IntIdTable("transaction_rule") {
    val userId = reference("user_id", UsersTable)
    val newCategoryId = reference("new_category_id", CategoriesTable)
    val name = varchar("name", 45)
    val forExpenses = bool("for_expenses")
    val forIncomes = bool("for_incomes")
    val keywords = varchar("keywords", 45)
    val priority = integer("priority")
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class TransactionRuleEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<TransactionRuleEntity>(TransactionRulesTable)

    var user by UserEntity referencedOn TransactionRulesTable.userId
    var newCategory by CategoryEntity referencedOn TransactionRulesTable.newCategoryId
    var name by TransactionRulesTable.name
    var forExpenses by TransactionRulesTable.forExpenses
    var forIncomes by TransactionRulesTable.forIncomes
    var keywords by TransactionRulesTable.keywords
    var priority by TransactionRulesTable.priority
    var createdAt by TransactionRulesTable.createdAt
    var modifiedAt by TransactionRulesTable.modifiedAt

    fun toModel() = TransactionRule(
        id.value,
        user.toModel(),
        newCategory.toModel(),
        name,
        forExpenses,
        forIncomes,
        keywords.split(";"),
        priority,
        createdAt,
        modifiedAt,
    )
}

class TransactionRule(
    val id: Int,
    val user: User,
    val newCategory: Category,
    val name: String,
    val forExpenses: Boolean,
    val forIncomes: Boolean,
    val keywords: List<String>,
    val priority: Int,
    val createdAt: Instant,
    val modifiedAt: Instant,
)