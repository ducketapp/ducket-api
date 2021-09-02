package io.budgery.api.domain.model.label

import domain.model.transaction.Transaction
import domain.model.transaction.TransactionEntity
import domain.model.transaction.TransactionsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object TransactionLabelsTable : IntIdTable("transaction_label") {
    val labelId = reference("record_label_id", LabelsTable)
    val transactionId = reference("transaction_id", TransactionsTable)
}

class TransactionLabelEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<TransactionLabelEntity>(TransactionLabelsTable)

    var label by LabelEntity referencedOn TransactionLabelsTable.labelId
    var transaction by TransactionEntity referencedOn TransactionLabelsTable.transactionId

    fun toModel() = TransactionLabel(id.value, label.toModel(), transaction.toModel())
}

data class TransactionLabel(
    val id: Int,
    val label: Label,
    val transaction: Transaction,
)