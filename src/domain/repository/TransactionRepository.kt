package io.budgery.api.domain.repository

import domain.model.account.AccountEntity
import domain.model.category.CategoryEntity
import domain.model.transaction.*
import domain.model.transaction.TransactionsTable
import domain.model.user.UserEntity
import io.budgery.api.domain.controller.record.TransactionCreateDto
import org.jetbrains.exposed.sql.and

import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class TransactionRepository {

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

    fun deleteOne(userId: Int, transactionId: Int): Boolean = transaction {
        TransactionEntity.find { TransactionsTable.id.eq(transactionId).and(TransactionsTable.userId.eq(userId)) }.firstOrNull()?.let {
            it.delete()
            findOne(userId, transactionId) == null
        } ?: false
    }

    fun getTotalByAccount(userId: Int, accountId: Int) : Int = transaction {
        findAllByAccount(userId, accountId).size
    }
}