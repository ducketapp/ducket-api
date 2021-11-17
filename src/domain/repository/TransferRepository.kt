package io.ducket.api.domain.repository

import domain.model.account.AccountEntity
import domain.model.user.UserEntity
import io.ducket.api.domain.controller.transfer.TransferCreateDto
import io.ducket.api.domain.model.attachment.Attachment
import io.ducket.api.domain.model.attachment.AttachmentEntity
import io.ducket.api.domain.model.attachment.AttachmentsTable
import io.ducket.api.domain.model.transfer.*
import io.ducket.api.domain.model.transfer.TransferAttachmentsTable
import io.ducket.api.domain.model.transfer.TransfersTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class TransferRepository : AttachmentRepository() {

    fun findOne(userId: String, transferId: String): Transfer? = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId).and(TransfersTable.id.eq(transferId))
        }.firstOrNull()?.toModel()
    }

    fun findAllByRelation(userId: String, relationId: String): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId).and(TransfersTable.relationId.eq(relationId))
        }.map { it.toModel() }
    }

    fun findAllByUserId(userId: String): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
        }.sortedByDescending { it.date }.map { it.toModel() }
    }

    fun findAllOutgoing(userId: String, accountId: String): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
                .and(TransfersTable.amount.less(0))
                .and(TransfersTable.accountId.eq(accountId))
        }.map { it.toModel() }
    }

    fun findAllIncoming(userId: String, accountId: String): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
                .and(TransfersTable.amount.greater(0))
                .and(TransfersTable.accountId.eq(accountId))
        }.map { it.toModel() }
    }

    fun findAllByAccount(userId: String, accountId: String): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId).and(TransfersTable.accountId.eq(accountId))
        }.sortedByDescending { it.date }.map { it.toModel() }
    }

    fun findAll(userId: String): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
        }.sortedByDescending { it.date }.map { it.toModel() }
    }

    fun getTotalByAccount(userId: String, accountId: String): Int = transaction {
        findAllByAccount(userId, accountId).size
    }

    fun create(userId: String, senderDto: TransferCreateDto, rate: BigDecimal): List<Transfer> = transaction {
        val relationId = UUID.randomUUID().toString().substring(0, 8).toUpperCase()

        val senderTransfer = createNewTransfer(userId, senderDto, rate, relationId).toModel()
        val recipientDto = senderDto.copy(
            accountId = senderDto.transferAccountId,
            transferAccountId = senderDto.accountId,
            amount = senderDto.amount.abs().multiply(rate),
            date = senderDto.date.plusMillis(1L), // hack to prevent excessive sorting
        )
        val recipientTransfer = createNewTransfer(userId, recipientDto, rate, relationId).toModel()

        return@transaction listOf(recipientTransfer, senderTransfer)
    }

    fun delete(userId: String, relationId: String) = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId).and(TransfersTable.relationId.eq(relationId))
        }.toList().forEach { it.delete() }
    }

    override fun findAttachment(userId: String, entityId: String, attachmentId: String): Attachment? = transaction {
        val query = AttachmentsTable.select {
            AttachmentsTable.id.eq(attachmentId)
                .and {
                    exists(TransferAttachmentsTable.select {
                        TransferAttachmentsTable.attachmentId.eq(attachmentId)
                            .and(TransferAttachmentsTable.transferId.eq(entityId))
                    })
                }
        }
        return@transaction AttachmentEntity.wrapRows(query).firstOrNull()?.toModel()
    }

    override fun getAttachmentsAmount(entityId: String): Int = transaction {
        TransferAttachmentsTable.select {
            TransferAttachmentsTable.transferId.eq(entityId)
        }.count().toInt()
    }

    override fun createAttachment(userId: String, entityId: String, newFile: File): Unit = transaction {
        val newAttachment = AttachmentEntity.new {
            filePath = newFile.path
            createdAt = Instant.now()
        }.toModel()

        TransferAttachmentsTable.insert {
            it[this.attachmentId] = AttachmentEntity[newAttachment.id].id
            it[this.transferId] = TransferEntity[entityId].id
        }

        TransferEntity.findById(entityId)?.also { found ->
            found.modifiedAt = Instant.now()
        }
    }

    override fun deleteAttachment(userId: String, entityId: String, attachmentId: String): Boolean = transaction {
        AttachmentsTable.deleteWhere { AttachmentsTable.id.eq(attachmentId) } > 0
    }

    private fun createNewTransfer(
        userId: String,
        dto: TransferCreateDto,
        rate: BigDecimal,
        relation: String
    ): TransferEntity {
        return TransferEntity.new(UUID.randomUUID().toString()) { // to prevent duplicate id
            account = AccountEntity[dto.accountId]
            transferAccount = AccountEntity[dto.transferAccountId]
            user = UserEntity[userId]
            relationId = relation
            amount = dto.amount
            exchangeRate = rate
            date = dto.date
            notes = dto.note
            longitude = dto.longitude
            latitude = dto.latitude
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }
    }
}