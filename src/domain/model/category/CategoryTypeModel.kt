package domain.model.category

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object CategoryTypesTable : IntIdTable("category_type") {
    val name = varchar("name", 45)
}

class CategoryTypeEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<CategoryTypeEntity>(CategoryTypesTable)

    var name by CategoryTypesTable.name

    fun toModel() = CategoryType(id.value, name)
}

class CategoryType(
    val id: Int,
    val name: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CategoryType

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        return result
    }
}