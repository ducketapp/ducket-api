package io.ducket.api.domain.model.label

import domain.model.transaction.Transaction
import domain.model.transaction.TransactionEntity
import domain.model.transaction.TransactionsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import java.util.*

internal object TransactionLabelsTable : Table("transaction_label") {
    val labelId = reference("record_label_id", LabelsTable)
    val transactionId = reference("transaction_id", TransactionsTable)

    override val primaryKey = PrimaryKey(labelId, transactionId)
}

/*
class TransactionLabelEntity(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<TransactionLabelEntity>(TransactionLabelsTable)

    var label by LabelEntity referencedOn TransactionLabelsTable.labelId
    var transaction by TransactionEntity referencedOn TransactionLabelsTable.transactionId

    fun toModel() = TransactionLabel(id.value, label.toModel(), transaction.toModel())
}

data class TransactionLabel(
    val id: UUID,
    val label: Label,
    val transaction: Transaction,
)*/
