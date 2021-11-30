package domain.model.rule

import domain.model.category.CategoriesTable
import domain.model.category.Category
import domain.model.category.CategoryEntity
import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.ducket.api.domain.model.StringIdTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

internal object RulesTable : StringIdTable("import_rule") {
    val userId = reference("user_id", UsersTable)
    val recordCategoryId = reference("record_category_id", CategoriesTable)
    val name = varchar("name", 45)
    val isExpense = bool("expense")
    val isIncome = bool("income")
    val keywords = varchar("keywords", 128)
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class RuleEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, RuleEntity>(RulesTable)

    var user by UserEntity referencedOn RulesTable.userId
    var recordCategory by CategoryEntity referencedOn RulesTable.recordCategoryId
    var name by RulesTable.name
    var isExpense by RulesTable.isExpense
    var isIncome by RulesTable.isIncome
    var keywords by RulesTable.keywords
    var createdAt by RulesTable.createdAt
    var modifiedAt by RulesTable.modifiedAt

    fun toModel() = Rule(
        id.value,
        user.toModel(),
        recordCategory.toModel(),
        name,
        isExpense,
        isIncome,
        keywords.split(" "),
        createdAt,
        modifiedAt,
    )
}

class Rule(
    val id: String,
    val user: User,
    val recordCategory: Category,
    val name: String,
    val isExpense: Boolean,
    val isIncome: Boolean,
    val keywords: List<String>,
    val createdAt: Instant,
    val modifiedAt: Instant,
)
