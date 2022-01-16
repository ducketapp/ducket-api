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
import io.ducket.api.utils.TransferCodeGenerator
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class TransferRepository(
    private val userRepository: UserRepository,
) : AttachmentRepository() {

    fun findOne(userId: Long, transferId: Long): Transfer? = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId).and(TransfersTable.id.eq(transferId))
        }.firstOrNull()?.toModel()
    }

    fun findAllByRelation(userId: Long, relationId: String): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId).and(TransfersTable.relationCode.eq(relationId))
        }.map { it.toModel() }
    }

    fun findAllByUserId(userId: Long): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
        }.sortedByDescending { it.date }.map { it.toModel() }
    }

    fun findAllIncludingObserved(userId: Long): List<Transfer> = transaction {
        val followedUsers = userRepository.findUsersFollowingByUser(userId)

        TransferEntity.wrapRows(
            TransfersTable.select {
                TransfersTable.userId.eq(userId)
                    .or(TransfersTable.userId.inList(followedUsers.map { it.id }))
            }
        ).toList().map { it.toModel() }
    }

    fun findAllOutgoing(userId: Long, accountId: Long): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
                .and(TransfersTable.amount.less(0))
                .and(TransfersTable.accountId.eq(accountId))
        }.map { it.toModel() }
    }

    fun findAllIncoming(userId: Long, accountId: Long): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
                .and(TransfersTable.amount.greater(0))
                .and(TransfersTable.accountId.eq(accountId))
        }.map { it.toModel() }
    }

    fun findAllByAccount(userId: Long, accountId: Long): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId).and(TransfersTable.accountId.eq(accountId))
        }.sortedByDescending { it.date }.map { it.toModel() }
    }

    fun findAll(userId: Long): List<Transfer> = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId)
        }.sortedByDescending { it.date }.map { it.toModel() }
    }

    fun getTotalByAccount(userId: Long, accountId: Long): Int = transaction {
        findAllByAccount(userId, accountId).size
    }

    fun create(userId: Long, senderDto: TransferCreateDto, rate: BigDecimal): List<Transfer> = transaction {
        val relationCode = TransferCodeGenerator.generate()

        val senderTransfer = createNewTransfer(userId, senderDto, rate, relationCode).toModel()
        val recipientDto = senderDto.copy(
            accountId = senderDto.transferAccountId,
            transferAccountId = senderDto.accountId,
            amount = senderDto.amount.abs().multiply(rate),
            date = senderDto.date.plusMillis(1L), // hack to prevent excessive sorting
        )
        val recipientTransfer = createNewTransfer(userId, recipientDto, rate, relationCode).toModel()

        return@transaction listOf(recipientTransfer, senderTransfer)
    }

    fun delete(userId: Long, relationCode: String) = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId).and(TransfersTable.relationCode.eq(relationCode))
        }.toList().forEach { it.delete() }
    }

    fun delete(userId: Long, transferId: Long) = transaction {
        TransferEntity.find {
            TransfersTable.userId.eq(userId).and(TransfersTable.id.eq(transferId))
        }.firstOrNull()?.delete()
    }

    override fun findAttachment(userId: Long, entityId: Long, attachmentId: Long): Attachment? = transaction {
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

    override fun getAttachmentsAmount(entityId: Long): Int = transaction {
        TransferAttachmentsTable.select {
            TransferAttachmentsTable.transferId.eq(entityId)
        }.count().toInt()
    }

    override fun createAttachment(userId: Long, entityId: Long, newFile: File): Unit = transaction {
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

    override fun deleteAttachment(userId: Long, entityId: Long, attachmentId: Long): Boolean = transaction {
        AttachmentsTable.deleteWhere { AttachmentsTable.id.eq(attachmentId) } > 0
    }

    private fun createNewTransfer(
        userId: Long,
        dto: TransferCreateDto,
        rate: BigDecimal,
        relation: String
    ): TransferEntity {
        return TransferEntity.new { // to prevent duplicate id
            account = AccountEntity[dto.accountId]
            transferAccount = AccountEntity[dto.transferAccountId]
            user = UserEntity[userId]
            relationCode = relation
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