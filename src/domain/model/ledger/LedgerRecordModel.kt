package io.ducket.api.domain.model.ledger

import domain.model.account.Account
import domain.model.account.AccountEntity
import domain.model.account.AccountsTable
import io.ducket.api.app.LedgerRecordType
import domain.model.operation.Operation
import domain.model.operation.OperationEntity
import domain.model.operation.OperationsTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.math.BigDecimal
import java.time.Instant

internal object LedgerRecordsTable : LongIdTable("ledger_record") {
    val operationId = reference("operation_id", OperationsTable)
    val transferAccountId = reference("transfer_account_id", AccountsTable).nullable()
    val accountId = reference("account_id", AccountsTable)
    val type = enumerationByName("type", 32, LedgerRecordType::class)
    val clearedFunds = decimal("cleared_funds", 10, 2)
    val postedFunds = decimal("posted_funds", 10, 2)
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val modifiedAt = timestamp("modified_at").clientDefault { Instant.now() }
}

class LedgerRecordEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<LedgerRecordEntity>(LedgerRecordsTable)

    var operation by OperationEntity referencedOn LedgerRecordsTable.operationId
    var transferAccount by AccountEntity optionalReferencedOn LedgerRecordsTable.transferAccountId
    var account by AccountEntity referencedOn LedgerRecordsTable.accountId
    var type by LedgerRecordsTable.type
    var clearedFunds by LedgerRecordsTable.clearedFunds
    var postedFunds by LedgerRecordsTable.postedFunds
    var createdAt by LedgerRecordsTable.createdAt
    var modifiedAt by LedgerRecordsTable.modifiedAt

    fun toModel() = LedgerRecord(
        id = id.value,
        operation = operation.toModel(),
        transferAccount = transferAccount?.toModel(),
        account = account.toModel(),
        type = type,
        clearedFunds = clearedFunds,
        postedFunds = postedFunds,
        createdAt = createdAt,
        modifiedAt = modifiedAt,
    )
}

data class LedgerRecord(
    val id: Long,
    val operation: Operation,
    val transferAccount: Account?,
    val account: Account,
    val type: LedgerRecordType,
    val clearedFunds: BigDecimal,
    val postedFunds: BigDecimal,
    val createdAt: Instant,
    val modifiedAt: Instant,
)