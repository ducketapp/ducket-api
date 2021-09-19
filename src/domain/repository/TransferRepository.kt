package io.budgery.api.domain.repository

import domain.model.account.AccountEntity
import domain.model.category.CategoryEntity
import domain.model.transaction.TransactionEntity
import domain.model.user.UserEntity
import io.budgery.api.domain.controller.transfer.TransferCreateDto
import io.budgery.api.domain.model.attachment.Attachment
import io.budgery.api.domain.model.attachment.AttachmentEntity
import io.budgery.api.domain.model.attachment.AttachmentsTable
import io.budgery.api.domain.model.transaction.TransactionAttachmentEntity
import io.budgery.api.domain.model.transaction.TransactionAttachmentsTable
import io.budgery.api.domain.model.transfer.*
import io.budgery.api.domain.model.transfer.TransferAttachmentsTable
import io.budgery.api.domain.model.transfer.TransfersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class TransferRepository: AttachmentRepository() {

    fun findOne(userId: Int, transferId: Int): Transfer? = transaction {
        TransferEntity.find { TransfersTable.userId.eq(userId).and(TransfersTable.id.eq(transferId)) }.firstOrNull()?.toModel()
    }

    fun findAllByRelation(userId: Int, relationUuid: UUID): List<Transfer> = transaction {
        TransferEntity.find { TransfersTable.userId.eq(userId).and(TransfersTable.relationUuid.eq(relationUuid.toString())) }.map { it.toModel() }
    }

    fun findAllByUserId(userId: Int): List<Transfer> = transaction {
        TransferEntity.find { TransfersTable.userId.eq(userId) }
            .map { it.toModel() }
    }

    fun findAllOutgoing(userId: Int, accountId: Int): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
                .and(TransfersTable.amount.less(0))
                .and(TransfersTable.accountId.eq(accountId)) }
            .map { it.toModel() }
    }

    fun findAllIncoming(userId: Int, accountId: Int): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
                .and(TransfersTable.amount.greater(0))
                .and(TransfersTable.accountId.eq(accountId)) }
            .map { it.toModel() }
    }

    fun findAllByAccount(userId: Int, accountId: Int): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
                .and(TransfersTable.accountId.eq(accountId)) }
            .map { it.toModel() }
    }

    fun findAll(userId: Int): List<Transfer> = transaction {
        TransferEntity.find { TransfersTable.userId.eq(userId) }.map { it.toModel() }
    }

    fun getTotalByAccount(userId: Int, accountId: Int): Int = transaction {
        findAllByAccount(userId, accountId).size
    }

    fun create(userId: Int, senderDto: TransferCreateDto, rate: BigDecimal): List<Transfer> = transaction {
        val relationUuid = UUID.randomUUID().toString()

        val senderTransfer = createNewTransfer(userId, senderDto, rate, relationUuid).toModel()
        val recipientDto = senderDto.copy(
            accountId = senderDto.transferAccountId,
            transferAccountId = senderDto.accountId,
            amount = senderDto.amount.abs() * rate
        )
        val recipientTransfer = createNewTransfer(userId, recipientDto, rate, relationUuid).toModel()

        return@transaction listOf(recipientTransfer, senderTransfer)
    }

    fun delete(userId: Int, relationUuid: UUID): Boolean = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
                .and(TransfersTable.relationUuid.eq(relationUuid.toString()))
        }.toList().forEach { it.delete() }

        return@transaction findAllByRelation(userId, relationUuid).isNullOrEmpty()
    }

    override fun findAttachment(entityId: Int, attachmentId: Int): Attachment? = transaction {
        val query = AttachmentsTable.select {
            AttachmentsTable.id.eq(attachmentId)
            exists(TransferAttachmentsTable.select {
                TransferAttachmentsTable.attachmentId.eq(attachmentId).and(TransferAttachmentsTable.transferId.eq(entityId))
            })
        }
        return@transaction AttachmentEntity.wrapRows(query).firstOrNull()?.toModel()
    }

    override fun getAttachmentsAmount(entityId: Int): Int = transaction {
        TransferAttachmentEntity.find { TransferAttachmentsTable.transferId.eq(entityId) }.count().toInt()
    }

    override fun createAttachment(entityId: Int, newFile: File, oldFileName: String): Unit = transaction {
        val newAttachment = AttachmentEntity.new {
            filePath = newFile.path
            originalFileName = oldFileName
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()

        TransferAttachmentEntity.new {
            attachment = AttachmentEntity[newAttachment.id]
            transfer = TransferEntity[entityId]
        }
    }

    private fun createNewTransfer(userId: Int, dto: TransferCreateDto, rate: BigDecimal, relation: String): TransferEntity {
        return TransferEntity.new {
            account = AccountEntity[dto.accountId]
            transferAccount = AccountEntity[dto.transferAccountId]
            user = UserEntity[userId]
            category = CategoryEntity[dto.categoryId]
            relationUuid = relation
            amount = dto.amount
            exchangeRate = rate
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