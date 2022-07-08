package domain.model.imports

import domain.model.category.CategoriesTable
import domain.model.category.Category
import domain.model.category.CategoryEntity
import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.ducket.api.app.ImportRuleLookupType
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

const val KEYWORDS_DELIMITER = ";"

internal object ImportRulesTable : LongIdTable("import_rule") {
    val userId = reference("user_id", UsersTable)
    val categoryId = reference("category_id", CategoriesTable)
    val title = varchar("title", 64)
    val lookupType = enumerationByName("lookup_type", 32, ImportRuleLookupType::class)
    val keywords = varchar("keywords", 512)
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val modifiedAt = timestamp("modified_at").clientDefault { Instant.now() }
}

class ImportRuleEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ImportRuleEntity>(ImportRulesTable)

    var user by UserEntity referencedOn ImportRulesTable.userId
    var category by CategoryEntity referencedOn ImportRulesTable.categoryId
    var title by ImportRulesTable.title
    var lookupType by ImportRulesTable.lookupType
    var keywords by ImportRulesTable.keywords
    var createdAt by ImportRulesTable.createdAt
    var modifiedAt by ImportRulesTable.modifiedAt

    fun toModel() = ImportRule(
        id.value,
        user.toModel(),
        category.toModel(),
        title,
        lookupType,
        keywords.split(KEYWORDS_DELIMITER),
        createdAt,
        modifiedAt,
    )
}

class ImportRule(
    val id: Long,
    val user: User,
    val category: Category,
    val title: String,
    val lookupType: ImportRuleLookupType,
    val keywords: List<String>,
    val createdAt: Instant,
    val modifiedAt: Instant,
)

class ImportRuleCreate(
    val userId: Long,
    val categoryId: Long,
    val title: String,
    val lookupType: ImportRuleLookupType,
    val keywords: List<String>,
)

class ImportRuleUpdate(
    val categoryId: Long,
    val title: String,
    val lookupType: ImportRuleLookupType,
    val keywords: List<String>,
)
