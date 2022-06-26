package io.ducket.api.domain.repository

import domain.model.account.AccountEntity
import domain.model.category.CategoriesTable
import domain.model.category.CategoryEntity
import io.ducket.api.app.LedgerRecordType
import io.ducket.api.domain.model.attachment.AttachmentsTable
import io.ducket.api.domain.model.ledger.LedgerRecord
import io.ducket.api.domain.model.ledger.LedgerRecordEntity
import io.ducket.api.domain.model.ledger.LedgerRecordsTable
import domain.model.operation.OperationAttachmentsTable
import domain.model.operation.OperationEntity
import domain.model.operation.OperationsTable
import domain.model.user.UserEntity
import io.ducket.api.app.database.Transactional
import io.ducket.api.domain.controller.ledger.LedgerRecordCreateDto
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

class LedgerRepository: Transactional {

    suspend fun findOne(userId: Long, ledgerRecordId: Long): LedgerRecord? = blockingTransaction {
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

    suspend fun findOneByOperation(userId: Long, ledgerRecordId: Long, operationId: Long): LedgerRecord? = blockingTransaction {
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

    // TODO suspend
    fun findAll(vararg userIds: Long): List<LedgerRecord> = transaction {
        LedgerRecordEntity.wrapRows(
            LedgerRecordsTable.select {
                exists(OperationsTable.select {
                    OperationsTable.userId.inList(userIds.asList())
                })
            }
        ).toList().map { it.toModel() }
    }

    suspend fun findAllByAccount(userId: Long, accountId: Long): List<LedgerRecord> = blockingTransaction {
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

    suspend fun delete(userId: Long, ledgerRecordId: Long, operationId: Long): Unit = blockingTransaction {
//        LedgerRecordEntity.find {
//            LedgerRecordsTable.id.eq(ledgerRecordId).and(LedgerRecordsTable.operationId.eq(operationId))
//        }.firstOrNull()?.also {
//            it.transferAccount?.id?.value?.also { transferAccountId ->
//                LedgerRecordEntity.find {
//                    LedgerRecordsTable.operationId.eq(operationId).and(LedgerRecordsTable.transferAccountId.eq(transferAccountId))
//                }.firstOrNull()?.delete()
//            }
//        }?.delete()
//
//        val attachmentIds = OperationEntity.find {
//            OperationsTable.userId.eq(userId).and(OperationsTable.id.eq(operationId))
//        }.flatMap { it.attachments }.map { it.id.value }
//
//        OperationAttachmentsTable.deleteWhere {
//            OperationAttachmentsTable.operationId.eq(operationId).and(OperationAttachmentsTable.attachmentId.inList(attachmentIds))
//        }
//
//        AttachmentsTable.deleteWhere { AttachmentsTable.id.inList(attachmentIds) }
        OperationsTable.deleteWhere {
            OperationsTable.userId.eq(userId).and(OperationsTable.id.eq(operationId))
        }
    }

    suspend fun createOne(operationId: Long,
                          accountId: Long,
                          transferAccountId: Long? = null,
                          type: LedgerRecordType,
                          clearedFunds: BigDecimal,
                          postedFunds: BigDecimal
    ): LedgerRecord = blockingTransaction {
        LedgerRecordEntity.new {
            this.operation = OperationEntity[operationId]
            this.account = AccountEntity[accountId]
            this.transferAccount = transferAccountId?.let { AccountEntity[it] }
            this.type = type
            this.clearedFunds = clearedFunds
            this.postedFunds = postedFunds
        }.toModel()
    }
}