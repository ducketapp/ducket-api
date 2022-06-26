package io.ducket.api.domain.repository

import domain.model.account.AccountsTable
import domain.model.currency.CurrenciesTable
import io.ducket.api.domain.controller.user.UserCreateDto
import io.ducket.api.domain.controller.user.UserUpdateDto
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import domain.model.currency.CurrencyEntity
import domain.model.imports.ImportRulesTable
import domain.model.imports.ImportsTable
import domain.model.user.User
import io.ducket.api.domain.model.attachment.AttachmentsTable
import io.ducket.api.domain.model.budget.BudgetsTable
import io.ducket.api.domain.model.group.GroupsTable
import io.ducket.api.domain.model.ledger.LedgerRecordEntity
import io.ducket.api.domain.model.ledger.LedgerRecordsTable
import domain.model.operation.OperationAttachmentsTable
import domain.model.operation.OperationsTable
import io.ducket.api.app.database.Transactional
import io.ducket.api.utils.HashUtils

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository: Transactional {

    suspend fun createOne(data: UserCreateDto): User = blockingTransaction {
        UserEntity.new {
            this.name = data.name
            this.phone = data.phone
            this.email = data.email
            this.mainCurrency = CurrencyEntity.find { CurrenciesTable.isoCode.eq(data.currencyIsoCode) }.first()
            this.passwordHash = HashUtils.hash(data.password)
        }.toModel()
    }

    suspend fun findOneByEmail(email: String): User? = blockingTransaction {
        UserEntity.find { UsersTable.email.eq(email) }.firstOrNull()?.toModel()
    }

    // TODO suspend
    fun findOne(userId: Long): User? = transaction {
        UserEntity.findById(userId)?.toModel()
    }

    suspend fun findAll(): List<User> = blockingTransaction {
        UserEntity.all().map { it.toModel() }
    }

    suspend fun updateOne(userId: Long, data: UserUpdateDto): User? = blockingTransaction {
        UserEntity.findById(userId)?.also { entity ->
            data.name?.let { entity.name = it }
            data.phone?.let { entity.phone = it }
            data.password?.let { entity.passwordHash = HashUtils.hash(it) }
        }?.toModel()
    }

    // TODO update
    suspend fun deleteData(userId: Long): Unit = blockingTransaction {
        LedgerRecordEntity.wrapRows(
            LedgerRecordsTable.select {
                exists(OperationsTable.select {
                    OperationsTable.userId.eq(userId)
                })
            }
        ).also {
            LedgerRecordsTable.deleteWhere {
                LedgerRecordsTable.id.inList(it.map { it.id.value })
            }

            AttachmentsTable.deleteWhere {
                exists(OperationAttachmentsTable.select {
                    OperationAttachmentsTable.operationId.inList(it.map { it.operation.id.value })
                })
            }

            OperationsTable.deleteWhere {
                OperationsTable.id.inList(it.map { it.operation.id.value })
            }
        }

        GroupsTable.deleteWhere { GroupsTable.ownerId.eq(userId) }
        BudgetsTable.deleteWhere { BudgetsTable.userId.eq(userId) }
        ImportsTable.deleteWhere { ImportsTable.userId.eq(userId) }
        ImportRulesTable.deleteWhere { ImportRulesTable.userId.eq(userId) }
        AccountsTable.deleteWhere { AccountsTable.userId.eq(userId) }
    }

    suspend fun deleteOne(userId: Long): Unit = blockingTransaction {
        UserEntity.findById(userId)?.delete()
    }
}
