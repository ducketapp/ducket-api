package io.ducket.api.domain.model.label

import domain.model.transaction.TransactionsTable
import org.jetbrains.exposed.sql.Table

internal object TransactionLabelsTable : Table("transaction_label") {
    val labelId = reference("record_label_id", LabelsTable)
    val transactionId = reference("transaction_id", TransactionsTable)

    override val primaryKey = PrimaryKey(labelId, transactionId)
}
