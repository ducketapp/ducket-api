package io.budgery.api.domain.repository

import domain.model.account.AccountEntity
import domain.model.category.CategoryEntity
import domain.model.transaction.*
import domain.model.transaction.TransactionsTable
import domain.model.user.UserEntity
import io.budgery.api.domain.controller.transaction.TransactionCreateDto
import io.budgery.api.domain.model.attachment.Attachment
import io.budgery.api.domain.model.attachment.AttachmentEntity
import io.budgery.api.domain.model.attachment.AttachmentsTable
import io.budgery.api.domain.model.budget.BudgetAccountEntity
import io.budgery.api.domain.model.budget.BudgetEntity
import io.budgery.api.domain.model.transaction.TransactionAttachmentEntity
import io.budgery.api.domain.model.transaction.TransactionAttachmentsTable
import kotlinx.coroutines.selects.select
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.select

import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.time.Instant

class TransactionRepository: AttachmentRepository() {

    fun findOne(userId: Int, transactionId: Int): Transaction? = transaction {
        TransactionEntity.find {
            TransactionsTable.userId.eq(userId)
                .and(TransactionsTable.id.eq(transactionId))
        }.firstOrNull()?.toModel()
    }

    fun findAll(userId: Int): List<Transaction> = transaction {
        TransactionEntity.find { TransactionsTable.userId.eq(userId) }.map { it.toModel() }
    }

    fun findAllByCategories(userId: Int, categoryIds: List<Int>): List<Transaction> = transaction {
        TransactionEntity.find {
            TransactionsTable.userId.eq(userId)
                .and(TransactionsTable.categoryId.inList(categoryIds))
        }.map { it.toModel() }
    }

    fun findAllByAccount(userId: Int, accountId: Int): List<Transaction> = transaction {
        TransactionEntity.find {
            TransactionsTable.userId.eq(userId)
                .and(TransactionsTable.accountId.eq(accountId))
        }.map { it.toModel() }
    }

    fun create(userId: Int, dto: TransactionCreateDto): Transaction = transaction {
        TransactionEntity.new {
            account = AccountEntity[dto.accountId]
            category = CategoryEntity[dto.categoryId]
            user = UserEntity[userId]
            transactionRule = null
            import = null
            amount = dto.amount
            date = dto.date
            payee = dto.payee
            note = dto.note
            longitude = dto.longitude
            latitude = dto.latitude
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()
    }

    fun getTotalByAccount(userId: Int, accountId: Int) : Int = transaction {
        findAllByAccount(userId, accountId).size
    }

    fun deleteOne(userId: Int, transactionId: Int): Boolean = transaction {
        TransactionEntity.find { TransactionsTable.id.eq(transactionId).and(TransactionsTable.userId.eq(userId)) }.firstOrNull()?.let {
            it.delete()
            findOne(userId, transactionId) == null
        } ?: false
    }

    override fun findAttachment(entityId: Int, attachmentId: Int): Attachment? = transaction {
        val query = AttachmentsTable.select {
            AttachmentsTable.id.eq(attachmentId)
            exists(TransactionAttachmentsTable.select {
                TransactionAttachmentsTable.attachmentId.eq(attachmentId).and(TransactionAttachmentsTable.transactionId.eq(entityId))
            })
        }
        return@transaction AttachmentEntity.wrapRows(query).firstOrNull()?.toModel()
    }

    override fun getAttachmentsAmount(entityId: Int): Int = transaction {
        TransactionAttachmentEntity.find { TransactionAttachmentsTable.transactionId.eq(entityId) }.count().toInt()
    }

    override fun createAttachment(entityId: Int, newFile: File, oldFileName: String): Unit = transaction {
        val newAttachment = AttachmentEntity.new {
            filePath = newFile.path
            originalFileName = oldFileName
            createdAt = Instant.now()
            modifiedAt = Instant.now()
        }.toModel()

        TransactionAttachmentEntity.new {
            attachment = AttachmentEntity[newAttachment.id]
            transaction = TransactionEntity[entityId]
        }
    }
}