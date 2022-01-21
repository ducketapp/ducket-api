package domain.model.imports

import domain.model.category.CategoriesTable
import domain.model.category.Category
import domain.model.category.CategoryEntity
import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

internal object ImportRulesTable : LongIdTable("import_rule") {
    val userId = reference("user_id", UsersTable)
    val recordCategoryId = reference("record_category_id", CategoriesTable)
    val name = varchar("name", 45)
    val isExpense = bool("expense")
    val isIncome = bool("income")
    val keywords = varchar("keywords", 512)
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class ImportRuleEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ImportRuleEntity>(ImportRulesTable)

    var user by UserEntity referencedOn ImportRulesTable.userId
    var recordCategory by CategoryEntity referencedOn ImportRulesTable.recordCategoryId
    var name by ImportRulesTable.name
    var isExpense by ImportRulesTable.isExpense
    var isIncome by ImportRulesTable.isIncome
    var keywords by ImportRulesTable.keywords
    var createdAt by ImportRulesTable.createdAt
    var modifiedAt by ImportRulesTable.modifiedAt

    fun toModel() = ImportRule(
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

class ImportRule(
    val id: Long,
    val user: User,
    val recordCategory: Category,
    val name: String,
    val isExpense: Boolean,
    val isIncome: Boolean,
    val keywords: List<String>,
    val createdAt: Instant,
    val modifiedAt: Instant,
)
