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
import io.ducket.api.domain.model.transaction.TransactionAttachmentsTable
import org.jetbrains.exposed.sql.*

import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.time.Instant
import java.util.*

class TransactionRepository : AttachmentRepository() {

    fun findOne(userId: String, transactionId: String): Transaction? = transaction {
        TransactionEntity.find {
            TransactionsTable.userId.eq(userId)
                .and(TransactionsTable.id.eq(transactionId))
        }.firstOrNull()?.toModel()
    }

    fun findAll(userId: String): List<Transaction> = transaction {
        TransactionEntity.find {
            TransactionsTable.userId.eq(userId)
        }.sortedByDescending { it.date }.map { it.toModel() }
    }

/*    fun findAllByCategories(userId: Int, categoryIds: List<Int>): List<Transaction> = transaction {
        TransactionEntity.find {
            TransactionsTable.userId.eq(userId).and(TransactionsTable.categoryId.inList(categoryIds))
        }.map { it.toModel() }
    }*/

    fun findAllByAccount(userId: String, accountId: String): List<Transaction> = transaction {
        TransactionEntity.find {
            TransactionsTable.userId.eq(userId).and(TransactionsTable.accountId.eq(accountId))
        }.sortedByDescending { it.date }.map { it.toModel() }
    }

    fun create(userId: String, dto: TransactionCreateDto): Transaction = transaction {
        TransactionEntity.new {
            account = AccountEntity[dto.accountId]
            category = CategoryEntity[dto.categoryId]
            user = UserEntity[userId]
            import = null
            amount = dto.amount
            date = dto.date
            payee = dto.payee
            payer = null
            notes = dto.notes
            longitude = dto.longitude
            latitude = dto.latitude
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()
    }

    fun getTotalByAccount(userId: String, accountId: String): Int = transaction {
        findAllByAccount(userId, accountId).size
    }

    fun delete(userId: String, vararg transactionIds: String): Unit = transaction {
        transactionIds.forEach { id ->
            TransactionEntity.find {
                TransactionsTable.id.eq(id).and(TransactionsTable.userId.eq(userId))
            }.firstOrNull()?.delete()
        }
    }

    override fun findAttachment(userId: String, entityId: String, attachmentId: String): Attachment? = transaction {
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

    override fun getAttachmentsAmount(entityId: String): Int = transaction {
        TransactionAttachmentsTable.select {
            TransactionAttachmentsTable.transactionId.eq(entityId)
        }.count().toInt()
    }

    override fun createAttachment(userId: String, entityId: String, newFile: File): Unit = transaction {
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

    override fun deleteAttachment(userId: String, entityId: String, attachmentId: String): Boolean = transaction {
        AttachmentsTable.deleteWhere { AttachmentsTable.id.eq(attachmentId) } > 0
    }
}