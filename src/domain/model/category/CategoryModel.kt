package domain.model.category

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

internal object CategoriesTable : LongIdTable("category") {
    val name = varchar("name", 64)
    val group = varchar("group", 64)
}

class CategoryEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<CategoryEntity>(CategoriesTable)

    var name by CategoriesTable.name
    var group by CategoriesTable.group

    fun toModel() = Category(
        id.value,
        name,
        group,
    )
}

data class Category(
    val id: Long,
    val name: String,
    val group: String,
)
