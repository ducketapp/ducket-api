package io.ducket.api.domain.repository

import domain.model.account.AccountEntity
import domain.model.category.CategoryEntity
import domain.model.transaction.*
import domain.model.transaction.Transaction
import domain.model.transaction.TransactionsTable
import domain.model.user.UserEntity
import io.ducket.api.domain.controller.transaction.TransactionCreateDto
import io.ducket.api.domain.model.attachment.Attachment
import io.ducket.api.domain.model.attachment.AttachmentEntity
import io.ducket.api.domain.model.attachment.AttachmentsTable
import io.ducket.api.domain.model.follow.FollowEntity
import io.ducket.api.domain.model.follow.FollowsTable
import io.ducket.api.domain.model.transaction.TransactionAttachmentsTable
import org.jetbrains.exposed.sql.*

import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.time.Instant
import java.util.*

class TransactionRepository(
    private val userRepository: UserRepository,
) : AttachmentRepository() {

    fun findOne(userId: Long, transactionId: Long): Transaction? = transaction {
        TransactionEntity.find {
            TransactionsTable.userId.eq(userId)
                .and(TransactionsTable.id.eq(transactionId))
        }.firstOrNull()?.toModel()
    }

    fun findAll(userId: Long): List<Transaction> = transaction {
        TransactionEntity.find {
            TransactionsTable.userId.eq(userId)
        }.sortedByDescending { it.date }.map { it.toModel() }
    }

    fun findAllIncludingObserved(userId: Long): List<Transaction> = transaction {
        val followedUsers = userRepository.findUsersFollowingByUser(userId)

        TransactionEntity.wrapRows(
            TransactionsTable.select {
                TransactionsTable.userId.eq(userId)
                    .or(TransactionsTable.userId.inList(followedUsers.map { it.id }))
            }
        ).toList().map { it.toModel() }
    }

/*    fun findAllByCategories(userId: Int, categoryIds: List<Int>): List<Transaction> = transaction {
        TransactionEntity.find {
            TransactionsTable.userId.eq(userId).and(TransactionsTable.categoryId.inList(categoryIds))
        }.map { it.toModel() }
    }*/

    fun findAllByAccount(userId: Long, accountId: Long): List<Transaction> = transaction {
        TransactionEntity.find {
            TransactionsTable.userId.eq(userId).and(TransactionsTable.accountId.eq(accountId))
        }.sortedByDescending { it.date }.map { it.toModel() }
    }

    fun create(userId: Long, dto: TransactionCreateDto): Transaction = transaction {
        TransactionEntity.new {
            account = AccountEntity[dto.accountId]
            category = CategoryEntity[dto.categoryId]
            user = UserEntity[userId]
            import = null
            amount = dto.amount
            date = dto.date
            payeeOrPayer = dto.payee
            notes = dto.notes
            longitude = dto.longitude
            latitude = dto.latitude
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()
    }

    fun getTotalByAccount(userId: Long, accountId: Long): Int = transaction {
        findAllByAccount(userId, accountId).size
    }

    fun delete(userId: Long, vararg transactionIds: Long): Unit = transaction {
        TransactionEntity.find {
            TransactionsTable.id.inList(transactionIds.asList()).and(TransactionsTable.userId.eq(userId))
        }.forEach { transaction ->
            transaction.attachments.forEach { it.delete() }
            transaction.delete()
        }
    }

    fun deleteAll(userId: Long): Unit = transaction {
        TransactionEntity.find {
            TransactionsTable.userId.eq(userId)
        }.forEach { transaction ->
            transaction.attachments.forEach { it.delete() }
            transaction.delete()
        }
    }

    override fun findAttachment(userId: Long, entityId: Long, attachmentId: Long): Attachment? = transaction {
        val query = AttachmentsTable.select {
            AttachmentsTable.id.eq(attachmentId)
                .and {
                    exists(TransactionAttachmentsTable.select {
                        TransactionAttachmentsTable.attachmentId.eq(attachmentId)
                            .and(TransactionAttachmentsTable.transactionId.eq(entityId))
                    })
                }
        }
        AttachmentEntity.wrapRows(query).firstOrNull()?.toModel()
    }

    override fun getAttachmentsAmount(entityId: Long): Int = transaction {
        TransactionAttachmentsTable.select {
            TransactionAttachmentsTable.transactionId.eq(entityId)
        }.count().toInt()
    }

    override fun createAttachment(userId: Long, entityId: Long, newFile: File): Unit = transaction {
        val newAttachment = AttachmentEntity.new {
            filePath = newFile.path
            createdAt = Instant.now()
        }.toModel()

        TransactionAttachmentsTable.insert {
            it[this.attachmentId] = AttachmentEntity[newAttachment.id].id.value
            it[this.transactionId] = TransactionEntity[entityId].id.value
        }

        TransactionEntity.findById(entityId)?.also { found ->
            found.modifiedAt = Instant.now()
        }
    }

    override fun deleteAttachment(userId: Long, entityId: Long, attachmentId: Long): Boolean = transaction {
        AttachmentsTable.deleteWhere { AttachmentsTable.id.eq(attachmentId) } > 0
    }
}