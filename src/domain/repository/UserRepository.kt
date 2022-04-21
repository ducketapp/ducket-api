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
import io.ducket.api.BCRYPT_HASH_ROUNDS
import io.ducket.api.domain.model.attachment.AttachmentsTable
import io.ducket.api.domain.model.budget.BudgetsTable
import io.ducket.api.domain.model.group.GroupEntity
import io.ducket.api.domain.model.group.GroupMembershipsTable
import io.ducket.api.domain.model.group.GroupsTable
import io.ducket.api.domain.model.ledger.LedgerRecordEntity
import io.ducket.api.domain.model.ledger.LedgerRecordsTable
import domain.model.operation.OperationAttachmentsTable
import domain.model.operation.OperationsTable

import io.ducket.api.getLogger
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant

class UserRepository {

    fun create(dto: UserCreateDto): User = transaction {
        UserEntity.new {
            this.name = dto.name
            this.phone = dto.phone
            this.email = dto.email
            this.mainCurrency = CurrencyEntity.find { CurrenciesTable.isoCode.eq(dto.currencyIsoCode) }.first()
            this.passwordHash = BCrypt.hashpw(dto.password, BCrypt.gensalt(BCRYPT_HASH_ROUNDS))
            Instant.now().also {
                this.createdAt = it
                this.modifiedAt = it
            }
        }.toModel()
    }

    fun findOneByEmail(email: String): User? = transaction {
        UserEntity.find { UsersTable.email.eq(email) }.firstOrNull()?.toModel()
    }

    fun findOne(userId: Long): User? = transaction {
        UserEntity.findById(userId)?.toModel()
    }

    fun findAll(): List<User> = transaction {
        UserEntity.all().map { it.toModel() }
    }

    fun updateOne(userId: Long, dto: UserUpdateDto): User? = transaction {
        UserEntity.findById(userId)?.also { found ->
            dto.name?.let {
                found.name = it
                found.modifiedAt = Instant.now()
            }
            dto.password?.let {
                found.passwordHash = BCrypt.hashpw(it, BCrypt.gensalt(BCRYPT_HASH_ROUNDS))
                found.modifiedAt = Instant.now()
            }
        }?.toModel()
    }

    fun deleteData(userId: Long): Unit = transaction {
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

        GroupEntity.find {
            GroupsTable.creatorId.eq(userId)
        }.map { it.id.value }.also {
            GroupMembershipsTable.deleteWhere { GroupMembershipsTable.groupId.inList(it).or(GroupMembershipsTable.memberId.eq(userId)) }
            GroupsTable.deleteWhere { GroupsTable.id.inList(it) }
        }

        BudgetsTable.deleteWhere { BudgetsTable.userId.eq(userId) }
        ImportsTable.deleteWhere { ImportsTable.userId.eq(userId) }
        ImportRulesTable.deleteWhere { ImportRulesTable.userId.eq(userId) }
        AccountsTable.deleteWhere { AccountsTable.userId.eq(userId) }
    }

    fun deleteOne(userId: Long): Unit = transaction {
        UserEntity.findById(userId)?.delete()
    }
}
