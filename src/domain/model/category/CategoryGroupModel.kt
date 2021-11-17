package domain.model.category

import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.ducket.api.domain.model.CombinedIdTable
import io.ducket.api.domain.model.StringIdTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

/*
internal object CategoryGroupsTable : StringIdTable("category_group") {
    val name = varchar("name", 45)
}

class CategoryGroupEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, CategoryGroupEntity>(CategoryGroupsTable)

    var name by CategoryGroupsTable.name

    fun toModel() = CategoryGroup(id.value, name)
}

class CategoryGroup(val id: String, val name: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CategoryGroup

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}*/
