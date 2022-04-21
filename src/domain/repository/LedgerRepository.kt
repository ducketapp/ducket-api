package io.ducket.api.domain.repository

import domain.model.account.AccountEntity
import io.ducket.api.app.LedgerRecordType
import io.ducket.api.domain.model.attachment.AttachmentsTable
import io.ducket.api.domain.model.ledger.LedgerRecord
import io.ducket.api.domain.model.ledger.LedgerRecordEntity
import io.ducket.api.domain.model.ledger.LedgerRecordsTable
import domain.model.operation.OperationAttachmentsTable
import domain.model.operation.OperationEntity
import domain.model.operation.OperationsTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.Instant

class LedgerRepository {

    fun findOne(userId: Long, ledgerRecordId: Long): LedgerRecord? = transaction {
        LedgerRecordEntity.wrapRows(
            LedgerRecordsTable.select {
                LedgerRecordsTable.id.eq(ledgerRecordId).and {
                    exists(OperationsTable.select {
                        OperationsTable.userId.eq(userId)
                    })
                }
            }
        ).firstOrNull()?.toModel()
    }

    fun findOneByOperation(userId: Long, ledgerRecordId: Long, operationId: Long): LedgerRecord? = transaction {
        LedgerRecordEntity.wrapRows(
            LedgerRecordsTable.select {
                LedgerRecordsTable.id.eq(ledgerRecordId).and {
                    exists(OperationsTable.select {
                        OperationsTable.userId.eq(userId).and(OperationsTable.id.eq(operationId))
                    })
                }
            }
        ).firstOrNull()?.toModel()
    }

    fun findAll(vararg userIds: Long): List<LedgerRecord> = transaction {
        LedgerRecordEntity.wrapRows(
            LedgerRecordsTable.select {
                exists(OperationsTable.select {
                    OperationsTable.userId.inList(userIds.asList())
                })
            }
        ).toList().map { it.toModel() }
    }

    fun findAllByAccount(userId: Long, accountId: Long): List<LedgerRecord> = transaction {
        LedgerRecordEntity.wrapRows(
            LedgerRecordsTable.select {
                LedgerRecordsTable.accountId.eq(accountId).and {
                    exists(OperationsTable.select {
                        OperationsTable.userId.eq(userId)
                    })
                }
            }
        ).toList().map { it.toModel() }
    }

    fun delete(userId: Long, ledgerRecordId: Long, operationId: Long): Unit = transaction {
        LedgerRecordEntity.find {
            LedgerRecordsTable.id.eq(ledgerRecordId).and(LedgerRecordsTable.operationId.eq(operationId))
        }.firstOrNull()?.also {
            it.transferAccount?.id?.value?.also { transferAccountId ->
                LedgerRecordEntity.find {
                    LedgerRecordsTable.operationId.eq(operationId).and(LedgerRecordsTable.transferAccountId.eq(transferAccountId))
                }.firstOrNull()?.delete()
            }
        }?.delete()

        val attachmentIds = OperationEntity.find {
            OperationsTable.userId.eq(userId).and(OperationsTable.id.eq(operationId))
        }.flatMap { it.attachments }.map { it.id.value }

        OperationAttachmentsTable.deleteWhere {
            OperationAttachmentsTable.operationId.eq(operationId).and(OperationAttachmentsTable.attachmentId.inList(attachmentIds))
        }

        AttachmentsTable.deleteWhere { AttachmentsTable.id.inList(attachmentIds) }
        OperationsTable.deleteWhere { OperationsTable.userId.eq(userId).and(OperationsTable.id.eq(operationId)) }
    }

    fun create(operationId: Long, accountId: Long, amount: BigDecimal, type: LedgerRecordType): LedgerRecord = transaction {
        LedgerRecordEntity.new {
            this.operation = OperationEntity[operationId]
            this.transferAccount = null
            this.account = AccountEntity[accountId]
            this.type = type
            this.amountPosted = amount
            this.amountTransferred = amount
            Instant.now().also {
                this.createdAt = it
                this.modifiedAt = it
            }
        }.toModel()
    }

    fun createTransfer(operationId: Long, transferAccountId: Long, accountId: Long, amount: BigDecimal, rate: BigDecimal): LedgerRecord = transaction {
        LedgerRecordEntity.new {
            this.operation = OperationEntity[operationId]
            this.transferAccount = AccountEntity[transferAccountId]
            this.account = AccountEntity[accountId]
            this.type = LedgerRecordType.EXPENSE
            this.amountPosted = amount
            this.amountTransferred = amount
            Instant.now().also {
                this.createdAt = it
                this.modifiedAt = it
            }
        }.also {
            LedgerRecordEntity.new {
                this.operation = OperationEntity[operationId]
                this.transferAccount = AccountEntity[accountId]
                this.account = AccountEntity[transferAccountId]
                this.type = LedgerRecordType.INCOME
                this.amountPosted = amount.multiply(rate)
                this.amountTransferred = amount
                Instant.now().also {
                    this.createdAt = it
                    this.modifiedAt = it
                }
            }
        }.toModel()
    }
}