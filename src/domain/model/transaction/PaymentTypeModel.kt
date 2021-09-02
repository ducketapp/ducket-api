package domain.model.transaction

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object PaymentTypesTable : IntIdTable("payment_type") {
    val name = varchar("name", 45)
}

class PaymentTypeEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<PaymentTypeEntity>(PaymentTypesTable)

    var name by PaymentTypesTable.name

    fun toModel() = PaymentType(id.value, name)
}

data class PaymentType(
    val id: Int,
    val name: String,
)