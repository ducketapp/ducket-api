package domain.model.category

import io.ducket.api.domain.model.StringIdTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

internal object CategoriesTable : StringIdTable("category") {
    val name = varchar("name", 45)
    val group = enumerationByName("group", 32, CategoryGroup::class)
}

class CategoryEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, CategoryEntity>(CategoriesTable)

    var name by CategoriesTable.name
    var group by CategoriesTable.group

    fun toModel() = Category(id.value, name, group)
}

class Category(
    val id: String,
    val name: String,
    val group: CategoryGroup,
)

enum class CategoryGroup {
    FOODSTUFF, SHOPPING, LIFE_ENTERTAINMENT, HOUSING, VEHICLE, TRANSPORT, TELECOM, FINANCIAL_COSTS, INVESTMENTS, INCOME, OTHER
}