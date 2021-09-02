package domain.model.category

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object CategoriesTable : IntIdTable("category") {
    val name = varchar("name", 45)
    val categoryTypeId = reference("category_type_id", CategoryTypesTable)
}

class CategoryEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<CategoryEntity>(CategoriesTable)

    var name by CategoriesTable.name
    var categoryType by CategoryTypeEntity referencedOn CategoriesTable.categoryTypeId

    fun toModel() = Category(id.value, name, categoryType.toModel())
}

class Category(
    val id: Int,
    val name: String,
    val categoryType: CategoryType,
)