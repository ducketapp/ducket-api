package io.budgery.api.domain.repository

import domain.model.account.AccountEntity
import domain.model.category.CategoryEntity
import domain.model.transaction.TransactionEntity
import domain.model.transaction.TransactionsTable
import domain.model.user.UserEntity
import io.budgery.api.domain.controller.record.TransferCreateDto
import io.budgery.api.domain.model.transfer.Transfer
import io.budgery.api.domain.model.transfer.TransferEntity
import io.budgery.api.domain.model.transfer.TransfersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class TransferRepository {

    fun findOne(userId: Int, transferId: Int): Transfer? = transaction {
        TransferEntity.find { TransfersTable.userId.eq(userId).and(TransfersTable.id.eq(transferId)) }.firstOrNull()?.toModel()
    }

    fun findAllByRelation(userId: Int, relationUuid: UUID): List<Transfer>? = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
                .and(TransfersTable.relationUuid.eq(relationUuid.toString()))
        }.toList().map { it.toModel() }
    }

    fun findAllByUserId(userId: Int): List<Transfer> = transaction {
        TransferEntity.find { TransfersTable.userId.eq(userId) }.map { it.toModel() }
    }

    fun findAllOutgoing(userId: Int, accountId: Int): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
                .and(TransfersTable.amount.less(0))
                .and(TransfersTable.accountId.eq(accountId))
        }.map { it.toModel() }
    }

    fun findAllIncoming(userId: Int, accountId: Int): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
                .and(TransfersTable.amount.greater(0))
                .and(TransfersTable.accountId.eq(accountId))
        }.map { it.toModel() }
    }

    fun findAllByAccount(userId: Int, accountId: Int): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
                .and(TransfersTable.accountId.eq(accountId))
        }.map { it.toModel() }
    }

    fun getTotalByAccount(userId: Int, accountId: Int): Int = transaction {
        findAllByAccount(userId, accountId).size
    }

    fun create(userId: Int, dto: TransferCreateDto): List<Transfer> = transaction {
        val relationUuid = UUID.randomUUID().toString()

        val senderTransfer = createNewTransfer(userId, dto, relationUuid).toModel()
        val recipientTransferDto = dto.copy(
            accountId = dto.transferAccountId,
            transferAccountId = dto.accountId,
            amount = dto.amount.abs() * dto.exchangeRate.toBigDecimal()
        )
        val recipientTransfer = createNewTransfer(userId, recipientTransferDto, relationUuid).toModel()

        return@transaction listOf(recipientTransfer, senderTransfer)
    }

    fun delete(userId: Int, relationUuid: UUID): Boolean = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
                .and(TransfersTable.relationUuid.eq(relationUuid.toString()))
        }.toList().forEach { it.delete() }

        return@transaction findAllByRelation(userId, relationUuid).isNullOrEmpty()
    }

    private fun createNewTransfer(userId: Int, dto: TransferCreateDto, relation: String): TransferEntity {
        return TransferEntity.new {
            account = AccountEntity[dto.accountId]
            transferAccount = AccountEntity[dto.transferAccountId]
            user = UserEntity[userId]
            category = CategoryEntity[dto.categoryId]
            relationUuid = relation
            amount = dto.amount
            exchangeRate = dto.exchangeRate
            date = dto.date
            payee = dto.payee
            note = dto.note
            longitude = dto.longitude
            latitude = dto.latitude
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }
    }
}