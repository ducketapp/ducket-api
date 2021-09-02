package domain.model.account

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object AccountTypesTable : IntIdTable("account_type") {
    val name = varchar("name", 45)
}

class AccountTypeEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<AccountTypeEntity>(AccountTypesTable)

    var name by AccountTypesTable.name

    fun toModel() = AccountType(id.value, name)
}

data class AccountType(
    val id: Int,
    val name: String,
)