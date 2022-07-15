package io.ducket.api.domain.model.debt

import io.ducket.api.domain.model.operation.OperationsTable
import org.jetbrains.exposed.sql.Table

internal object DebtOperationsTable : Table("debt_operation") {
    val debtId = reference("debt_id", DebtsTable.id)
    val operationId = reference("operation_id", OperationsTable.id)

    override val primaryKey = PrimaryKey(debtId, operationId)
}